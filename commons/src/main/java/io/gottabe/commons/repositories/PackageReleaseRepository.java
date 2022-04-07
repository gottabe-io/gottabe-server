package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageRelease;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageReleaseRepository extends CrudRepository<PackageRelease, Long> {

    Optional<PackageRelease> findByPackageDataGroupNameAndPackageDataNameAndVersion(String groupName, String packageName, String version);

    Optional<PackageRelease> findOneByPackageDataOrderByReleaseDateDesc(PackageData pack);
}
