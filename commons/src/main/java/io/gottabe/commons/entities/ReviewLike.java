package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbs_review_like", indexes = {
        @Index(unique = true, columnList = "review_id, user_id", name = "idx_reviewlike_user")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewLike extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PackageReleaseReview review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    private int rate;

}
