package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.entities.PackageReleaseReview;
import io.gottabe.commons.vo.StarsVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageReleaseReviewRepository extends CrudRepository<PackageReleaseReview, Long> {

    Page<PackageReleaseReview> findByRelease(PackageRelease release, Pageable pageable);

    @Query("select new io.gottabe.commons.vo.StarsVO(avg(rev.rate), count(rev.id), sum(case rev.vulnerability when true then 1 else 0 end)) " +
            "from PackageReleaseReview rev " +
            "join rev.release rel " +
            "where rel.packageData.name = :packageName " +
            "  and rel.packageData.group.name = :groupName " +
            "  and rel.version = :version")
    StarsVO findStars(String groupName, String packageName, String version);
}
