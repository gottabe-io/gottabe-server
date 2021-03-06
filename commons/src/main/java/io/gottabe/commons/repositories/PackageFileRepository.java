package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageFileRepository extends CrudRepository<PackageFile, Long> {

    @Query("select pf from PackageFile pf " +
            "where pf.release.version = :version " +
            "and pf.release.packageData.name = :packageName " +
            "and pf.release.packageData.group.name = :groupName " +
            "and pf.name = :name")
    Optional<PackageFile> findByGroupPackageVersionAndName(String groupName, String packageName, String version, String name);

    @Query("select pf from PackageFile pf " +
            "where pf.release.version = :version " +
            "and pf.release.packageData.name = :packageName " +
            "and pf.release.packageData.group.name = :groupName ")
    List<PackageFile> findByGroupPackageAndVersion(String groupName, String packageName, String version);
}
