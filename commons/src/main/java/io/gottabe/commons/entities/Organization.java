package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbs_organization")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Organization extends BaseOwner {

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User owner;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<OrganizationUser> users;

}
