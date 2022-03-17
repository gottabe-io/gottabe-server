package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.Organization;
import io.gottabe.commons.entities.OrganizationUser;
import io.gottabe.commons.entities.User;
import io.gottabe.commons.enums.OrgUserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationUserRepository extends CrudRepository<OrganizationUser, Long> {

    Optional<OrganizationUser> findByUserAndOrganization(User user, Organization organization);

    boolean existsByUserAndOrganizationAndRoleIn(User user, Organization organization, List<OrgUserRole> roles);

    List<OrganizationUser> findByUser(User user);
}
