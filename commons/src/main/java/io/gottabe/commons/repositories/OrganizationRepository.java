package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Long> {

    Optional<Organization> findByNickname(String nickname);

}
