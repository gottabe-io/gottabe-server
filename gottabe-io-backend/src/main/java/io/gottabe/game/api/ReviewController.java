package io.gottabe.game.api;

import io.gottabe.commons.entities.PackageReleaseReview;
import io.gottabe.commons.entities.ReviewLike;
import io.gottabe.commons.entities.User;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.ReviewMapper;
import io.gottabe.commons.services.ReviewService;
import io.gottabe.commons.services.UserService;
import io.gottabe.commons.vo.NewReviewVO;
import io.gottabe.commons.vo.ReviewLikeVO;
import io.gottabe.commons.vo.ReviewVO;
import io.gottabe.commons.vo.StarsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @GetMapping("{groupName}/{packageName}/{version}/stars")
    @Transactional
    public ResponseEntity<StarsVO> getStars(@PathVariable("groupName") String groupName,
                                            @PathVariable("packageName") String packageName,
                                            @PathVariable("version") String version) {
        StarsVO stars = reviewService.getStars(groupName, packageName, version);
        return ResponseEntity.ok(stars);
    }

    @GetMapping("{groupName}/{packageName}/{version}")
    @Transactional
    public ResponseEntity<List<ReviewVO>> findReviews(@PathVariable("groupName") String groupName,
                                                      @PathVariable("packageName") String packageName,
                                                      @PathVariable("version") String version,
                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "25") Integer size) {
        Page<PackageReleaseReview> reviews = reviewService.findRevisions(groupName, packageName, version, page, size);
        User user = userService.currentUser();
        List<ReviewVO> list = reviews.stream().map(review -> {
            ReviewVO vo = ReviewMapper.INSTANCE.reviewToVO(review);
            reviewService.findMyReviewRate(user, review).ifPresent(like -> vo.setMyRate(like.getRate()));
            return vo;
        }).collect(Collectors.toList());
        return ResponseEntity.ok().header("RESULT_COUNT", String.valueOf(reviews.getTotalElements())).body(list);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("{groupName}/{packageName}/{version}")
    @Transactional
    public ResponseEntity<Void> createReview(@PathVariable("groupName") String groupName,
                                                       @PathVariable("packageName") String packageName,
                                                       @PathVariable("version") String version,
                                                       @Valid @RequestBody NewReviewVO reviewVo) {
        PackageReleaseReview review = reviewService.createReview(groupName, packageName, version, reviewVo);
        return ResponseEntity.created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{unitId}")
                        .buildAndExpand(review.getId())
                        .toUri())
                .build();
    }

    @GetMapping("{groupName}/{packageName}/{version}/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ReviewVO> loadReview(@PathVariable("groupName") String groupName,
                                                 @PathVariable("packageName") String packageName,
                                                 @PathVariable("version") String version,
                                                 @PathVariable("id") Long id) {
        PackageReleaseReview review = reviewService.findById(id).orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity.ok(ReviewMapper.INSTANCE.reviewToVO(review));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("{groupName}/{packageName}/{version}/{id}")
    @Transactional
    public ResponseEntity<Void> rateReview(@PathVariable("groupName") String groupName,
                                           @PathVariable("packageName") String packageName,
                                           @PathVariable("version") String version,
                                           @PathVariable("id") Long id,
                                           @RequestBody ReviewLikeVO like) {
        reviewService.rateReview(groupName, packageName, version, id, like);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("{groupName}/{packageName}/{version}/{id}/my-rate")
    @Transactional
    public ResponseEntity<ReviewLikeVO> getMyReviewRate(@PathVariable("groupName") String groupName,
                                                @PathVariable("packageName") String packageName,
                                                @PathVariable("version") String version,
                                                @PathVariable("id") Long id) {
        ReviewLikeVO vo = reviewService.getMyReviewRate(groupName, packageName, version, id);
        return ResponseEntity.ok(vo);
    }

}
