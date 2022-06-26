package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbs_package_release_review", indexes = {
        @Index(columnList = "release_id, user_id", name = "idx_pkgreleasereview_user")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageReleaseReview extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PackageRelease release;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    private int rate;

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String review;

    private boolean vulnerability;

    private int reviewLikes;

    private int reviewDislikes;

}
