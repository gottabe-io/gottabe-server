package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.entities.PackageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageDataRepository extends CrudRepository<PackageData, Long> {

    Page<PackageData> findByGroupNameLike(String groupName, Pageable page);

    Optional<PackageData> findByGroupNameAndName(String groupName, String name);

    Page<PackageData> findByGroupOwner(BaseOwner owner, Pageable page);

    boolean existsByGroupNameAndName(String groupName, String packageName);
}
