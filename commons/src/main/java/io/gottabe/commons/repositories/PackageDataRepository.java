package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.enums.PackageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageDataRepository extends CrudRepository<PackageData, Long> {

    Page<PackageData> findByGroupNameLikeAndType(String groupName, PackageType type, Pageable page);

    Optional<PackageData> findByGroupNameAndNameAndType(String groupName, String name, PackageType type);

    Page<PackageData> findByGroupOwnerAndType(BaseOwner owner, PackageType type, Pageable page);

    boolean existsByGroupNameAndName(String groupName, String packageName);

    Optional<PackageData> findOneByGroupNameAndName(String groupName, String name);

    @Query("select p " +
            "from PackageData p " +
            "where p.name like :query " +
            "   or p.group.name like :query " +
            "   or p.group.owner.nickname like :query " +
            "order by p.group.owner.nickname, p.group.name, p.name")
    Page<PackageData> findByQuery(String query, Pageable page);
}
