package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageGroupRepository extends CrudRepository<PackageGroup, Long> {

    Optional<PackageGroup> findByName(String packageName);

    Page<PackageGroup> findByNameStartsWith(String groupName, Pageable pageable);
}
