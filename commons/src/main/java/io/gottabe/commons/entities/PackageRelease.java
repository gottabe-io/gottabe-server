package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tbs_package_release", indexes = {
        @Index(unique = true, columnList = "package_data_id, version", name = "uk_packagerelease_version")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PackageRelease extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private PackageData packageData;

    @Temporal(TemporalType.TIMESTAMP)
    private Date releaseDate;

    @Column(length = 1000)
    private String description;

    @Column(length = 2083)
    private String sourceUrl;

    @Column(length = 2083)
    private String issuesUrl;

    @Column(length = 2083)
    private String documentationUrl;

    @Column(length = 100)
    private String license;

    @OneToMany(mappedBy = "release", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PackageFile> files;

}
