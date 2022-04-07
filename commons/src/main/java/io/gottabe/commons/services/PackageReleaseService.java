package io.gottabe.commons.services;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.PackageReleaseMapper;
import io.gottabe.commons.repositories.PackageReleaseRepository;
import io.gottabe.commons.vo.PackageReleaseVO;
import io.gottabe.commons.vo.build.BuildDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PackageReleaseService extends AbstractCrudService<PackageRelease, Long> {

    @Autowired
    public PackageReleaseService(PackageReleaseRepository repository) {
        super(repository);
    }

    protected PackageReleaseRepository getRepository() {
        return (PackageReleaseRepository) this.repository;
    }

    public PackageReleaseVO findByGroupAndNameAndVersionVO(String groupName, String packageName, String version) {
        return getRepository().findByPackageDataGroupNameAndPackageDataNameAndVersion(groupName, packageName, version)
                .map(PackageReleaseMapper.INSTANCE::releaseToVO)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Optional<PackageRelease> findByGroupAndNameAndVersion(String groupName, String packageName, String version) {
        return getRepository().findByPackageDataGroupNameAndPackageDataNameAndVersion(groupName, packageName, version);
    }

    public PackageRelease createRelease(String version, BuildDescriptor build, PackageData packageData) {
        return save(PackageRelease.builder()
                .releaseDate(new Date())
                .packageData(packageData)
                .version(version)
                .description(build.getDescription())
                .issuesUrl(build.getIssueUrl())
                .sourceUrl(build.getSourceUrl())
                .documentationUrl(build.getDocumentationUrl())
                .build());
    }

    public Optional<PackageRelease> findOneByPackageDataOrderByReleaseDateDesc(PackageData pack) {
        return getRepository().findOneByPackageDataOrderByReleaseDateDesc(pack);
    }
}
