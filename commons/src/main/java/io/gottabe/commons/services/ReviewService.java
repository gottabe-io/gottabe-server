package io.gottabe.commons.services;

import io.gottabe.commons.entities.*;
import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.ReviewMapper;
import io.gottabe.commons.repositories.PackageReleaseRepository;
import io.gottabe.commons.repositories.PackageReleaseReviewRepository;
import io.gottabe.commons.repositories.ReviewLikeRepository;
import io.gottabe.commons.vo.NewReviewVO;
import io.gottabe.commons.vo.ReviewLikeVO;
import io.gottabe.commons.vo.StarsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService extends AbstractCrudService<PackageReleaseReview, Long> {

    private final ReviewLikeRepository reviewLikeRepository;
    private final PackageReleaseRepository packageReleaseRepository;
    private final UserService userService;

    protected PackageReleaseReviewRepository getRepository() {
        return (PackageReleaseReviewRepository) repository;
    }

    @Autowired
    public ReviewService(PackageReleaseReviewRepository repository, ReviewLikeRepository reviewLikeRepository, PackageReleaseRepository packageReleaseRepository, UserService userService) {
        super(repository);
        this.reviewLikeRepository = reviewLikeRepository;
        this.packageReleaseRepository = packageReleaseRepository;
        this.userService = userService;
    }

    public Page<PackageReleaseReview> findRevisions(String groupName, String packageName, String version, int page, int size) {
        PackageRelease release = packageReleaseRepository.findOneByPackageDataGroupNameAndPackageDataNameAndVersion(groupName, packageName, version).orElseThrow(ResourceNotFoundException::new);
        return getRepository().findByRelease(release, PageRequest.of(page, size, Sort.by(Sort.Order.desc("reviewLikes"))));
    }

    public PackageReleaseReview createReview(String groupName, String packageName, String version, NewReviewVO reviewVo) {
        User user = userService.currentUser();
        PackageRelease release = packageReleaseRepository.findOneByPackageDataGroupNameAndPackageDataNameAndVersion(groupName, packageName, version).orElseThrow(ResourceNotFoundException::new);
        BaseOwner owner = release.getPackageData().getGroup().getOwner();
        if (owner instanceof User) {
            if (user.equals(owner))
                throw new InvalidRequestException("review.error.owner");
        } else if (owner instanceof Organization) {
            Organization org = (Organization) owner;
            if (org.getUsers().stream().anyMatch(orgUser -> orgUser.getUser().equals(user)))
                throw new InvalidRequestException("review.error.owner");
        }
        PackageReleaseReview review = ReviewMapper.INSTANCE.voToEntity(reviewVo);
        review.setRelease(release);
        review.setUser(user);
        return save(review);
    }

    public void rateReview(String groupName, String packageName, String version, Long id, ReviewLikeVO rate) {
        User user = userService.currentUser();
        PackageReleaseReview review = findById(id).orElseThrow(ResourceNotFoundException::new);
        ReviewLike like = reviewLikeRepository.findOneByUserAndReview(user, review)
                .orElse(null);
        if (like != null) {
            if (like.getRate() > 0)
                review.setReviewLikes(review.getReviewLikes() - 1);
            else if (like.getRate() < 0)
                review.setReviewDislikes(review.getReviewDislikes() - 1);
        } else {
            like = ReviewLike.builder()
                    .review(review)
                    .user(user)
                    .build();
        }
        like.setRate(rate.getRate());
        if (like.getRate() > 0)
            review.setReviewLikes(review.getReviewLikes() + 1);
        else if (like.getRate() < 0)
            review.setReviewDislikes(review.getReviewDislikes() + 1);
        reviewLikeRepository.save(like);
    }

    public StarsVO getStars(String groupName, String packageName, String version) {
        return getRepository().findStars(groupName, packageName, version);
    }

    public ReviewLikeVO getMyReviewRate(String groupName, String packageName, String version, Long id) {
        User user = userService.currentUser();
        PackageReleaseReview review = findById(id).orElseThrow(ResourceNotFoundException::new);
        return reviewLikeRepository.findOneByUserAndReview(user, review).map(reviewLike -> ReviewLikeVO.builder().rate(reviewLike.getRate()).build())
                .orElse(ReviewLikeVO.builder().rate(0).build());
    }

    public Optional<ReviewLike> findMyReviewRate(User user, PackageReleaseReview review) {
        return reviewLikeRepository.findOneByUserAndReview(user, review);
    }
}
