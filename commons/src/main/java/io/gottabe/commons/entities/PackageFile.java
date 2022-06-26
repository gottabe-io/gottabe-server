package io.gottabe.commons.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tbs_package_file", indexes = {
        @Index(unique = true, columnList = "release_id, name", name = "idx_packagefile_releasename")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PackageFile extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private PackageRelease release;

    @Column(length = 200)
    private String name;

    private Long length;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

}
