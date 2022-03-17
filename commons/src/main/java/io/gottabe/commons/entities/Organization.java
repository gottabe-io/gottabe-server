package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbs_organization")
@Getter
@Setter
@EqualsAndHashCode(of = { "getId()" }, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Organization extends BaseOwner {

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<OrganizationUser> users;

}
