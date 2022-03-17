package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbs_package_group", indexes = {
        @Index(unique = false, columnList = "owner_id", name = "idx_group_owner"),
        @Index(unique = true, columnList = "name", name = "idx_group_name")
})
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PackageGroup extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private BaseOwner owner;

}
