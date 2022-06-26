package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageReleaseReview;
import io.gottabe.commons.entities.ReviewLike;
import io.gottabe.commons.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends CrudRepository<ReviewLike, Long> {

    Optional<ReviewLike> findOneByUserAndReview(User user, PackageReleaseReview review);

}
