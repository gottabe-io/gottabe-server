package io.gottabe.commons.entities;

import io.gottabe.commons.enums.OrgUserRole;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbs_organization_user", indexes = @Index(unique = true, name = "uk_orguser_org_user", columnList = "organization_id, user_id"))
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrganizationUser extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private OrgUserRole role;

    private boolean active;

    private LocalDateTime inviteDate;

}
