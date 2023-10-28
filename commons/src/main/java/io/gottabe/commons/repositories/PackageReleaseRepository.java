package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.enums.PackageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PackageReleaseRepository extends CrudRepository<PackageRelease, Long> {

    @Query("select pr from PackageRelease pr " +
            "where pr.packageData.group.name = :groupName " +
            "  and pr.packageData.name = :packageName " +
            "  and pr.version like :version " +
            "  and pr.packageData.type in (:types) " +
            " order by pr.version desc")
    List<PackageRelease> findByLatestPackageVersionWithType(String groupName, String packageName, String version, Set<PackageType> types, Pageable pageable);

    Optional<PackageRelease> findOneByPackageDataOrderByReleaseDateDesc(PackageData pack);

    @Query("select pr from PackageRelease pr " +
            "where pr.packageData.group.name = :groupName " +
            "  and pr.packageData.name = :packageName " +
            "  and pr.version like :version " +
            " order by pr.version desc")
    List<PackageRelease> findByLatestPackageVersion(String groupName, String packageName, String version, Pageable pageable);
}
