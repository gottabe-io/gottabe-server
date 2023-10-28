package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageFile;
import io.gottabe.commons.enums.PackageType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PackageFileRepository extends CrudRepository<PackageFile, Long> {

    @Query("select pf from PackageFile pf " +
            "where pf.release.version = :version " +
            "and pf.release.packageData.name = :packageName " +
            "and pf.release.packageData.group.name = :groupName " +
            "and pf.name = :name " +
            "and pf.release.packageData.type in (:types)")
    Optional<PackageFile> findByGroupPackageVersionAndNameAndPackageType(String groupName, String packageName, String version, String name, Set<PackageType> types);

    @Query("select pf from PackageFile pf " +
            "where pf.release.version = :version " +
            "and pf.release.packageData.name = :packageName " +
            "and pf.release.packageData.group.name = :groupName ")
    List<PackageFile> findByGroupPackageAndVersion(String groupName, String packageName, String version);
}
