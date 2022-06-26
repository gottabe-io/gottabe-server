package io.gottabe.commons.services;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.enums.PackageType;
import io.gottabe.commons.mapper.PackageDataMapper;
import io.gottabe.commons.repositories.PackageDataRepository;
import io.gottabe.commons.vo.PackageDataVO;
import io.gottabe.commons.vo.PagedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PackageDataService extends AbstractCrudService<PackageData, Long> {

    @Autowired
    public PackageDataService(PackageDataRepository repository) {
        super(repository);
    }

    protected PackageDataRepository getRepository() {
        return (PackageDataRepository) this.repository;
    }

    public Page<PackageData> findByGroupLike(String groupName, PackageType type, int page, int size) {
        return getRepository().findByGroupNameLikeAndType(groupName, type, PageRequest.of(page, size));
    }

    public Optional<PackageData> findByGroupAndName(String groupName, String packageName, PackageType type) {
        return getRepository().findByGroupNameAndNameAndType(groupName, packageName, type);
    }

    public boolean existsByGroupAndName(String groupName, String packageName) {
        return getRepository().existsByGroupNameAndName(groupName, packageName);
    }

    public PackageData getOrCreate(PackageGroup group, String packageName, PackageType type) {
        return getRepository().findByGroupNameAndNameAndType(group.getName(), packageName, type).orElseGet(() -> create(group, packageName, type));
    }

    private PackageData create(PackageGroup group, String packageName, PackageType type) {
        return save(PackageData.builder().group(group).name(packageName).type(type).build());
    }

    public Page<PackageData> findByOwnerAndType(BaseOwner owner, PackageType type, int page, int size) {
        return getRepository().findByGroupOwnerAndType(owner, type, PageRequest.of(page, size));
    }

    public Optional<PackageData> findOneByGroupNameAndName(String groupName, String packageName) {
        return getRepository().findOneByGroupNameAndName(groupName, packageName);
    }

    public PagedVO<PackageDataVO> findByQuery(String query, Integer page, Integer size) {
        PagedVO<PackageDataVO> pages = new PagedVO<>();
        Page<PackageData> results = getRepository().findByQuery(query, PageRequest.of(page, size));
        pages.setCurrentPage(page);
        pages.setTotalResults(results.getTotalPages());
        pages.setList(results.stream().map(PackageDataMapper.INSTANCE::packageToVOWithReleases).collect(Collectors.toList()));
        return pages;
    }
}
