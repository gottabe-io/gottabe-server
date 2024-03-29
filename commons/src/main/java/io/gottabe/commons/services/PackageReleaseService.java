package io.gottabe.commons.services;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.enums.PackageType;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.PackageReleaseMapper;
import io.gottabe.commons.repositories.PackageDataRepository;
import io.gottabe.commons.repositories.PackageReleaseRepository;
import io.gottabe.commons.vo.PackageReleaseVO;
import io.gottabe.commons.vo.build.BuildDescriptor;
import io.gottabe.commons.vo.build.PluginDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class PackageReleaseService extends AbstractCrudService<PackageRelease, Long> {

    private PackageDataRepository packageDataRepository;

    @Autowired
    public PackageReleaseService(PackageReleaseRepository repository, PackageDataRepository packageDataRepository) {
        super(repository);
        this.packageDataRepository = packageDataRepository;
    }

    protected PackageReleaseRepository getRepository() {
        return (PackageReleaseRepository) this.repository;
    }

    public PackageReleaseVO findByGroupAndNameAndVersionVO(String groupName, String packageName, String version, Set<PackageType> types) {
        return getRepository().findByLatestPackageVersionWithType(groupName, packageName, wildCardVersion(version), types, PageRequest.of(0,1))
                .stream().findFirst()
                .map(PackageReleaseMapper.INSTANCE::releaseToVO)
                .orElseThrow(ResourceNotFoundException::new);
    }

    private String wildCardVersion(String version) {
        return version.replaceAll("[*+]", "%");
    }

    public Optional<PackageRelease> findByGroupAndNameAndVersion(String groupName, String packageName, String version, Set<PackageType> types) {
        return getRepository().findByLatestPackageVersionWithType(groupName, packageName, wildCardVersion(version), types, PageRequest.of(0,1)).stream().findFirst();
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
                .license(build.getLicense())
                .build());
    }

    public PackageRelease createRelease(String version, PluginDescriptor plugin, PackageData packageData) {
        return save(PackageRelease.builder()
                .releaseDate(new Date())
                .packageData(packageData)
                .version(version)
                .description(plugin.getDescription())
                .issuesUrl(plugin.getIssueUrl())
                .sourceUrl(plugin.getSourceUrl())
                .documentationUrl(plugin.getDocumentationUrl())
                .license(plugin.getLicense())
                .build());
    }

    public Optional<PackageRelease> findOneByPackageDataOrderByReleaseDateDesc(PackageData pack) {
        return getRepository().findOneByPackageDataOrderByReleaseDateDesc(pack);
    }
}
