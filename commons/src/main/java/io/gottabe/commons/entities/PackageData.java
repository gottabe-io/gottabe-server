package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbs_package_data", indexes = @Index(unique = true, columnList = "group_id, name", name = "idx_packdata_groupname"))
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PackageData extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private PackageGroup group;

    @OneToMany(mappedBy = "packageData", fetch = FetchType.LAZY)
    private List<PackageRelease> releases;

}