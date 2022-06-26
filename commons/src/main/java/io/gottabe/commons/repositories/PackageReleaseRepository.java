package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.enums.PackageType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageReleaseRepository extends CrudRepository<PackageRelease, Long> {

    Optional<PackageRelease> findByPackageDataGroupNameAndPackageDataNameAndVersionAndPackageDataType(String groupName, String packageName, String version, PackageType type);

    Optional<PackageRelease> findOneByPackageDataOrderByReleaseDateDesc(PackageData pack);

    Optional<PackageRelease> findOneByPackageDataGroupNameAndPackageDataNameAndVersion(String groupName, String packageName, String version);
}
