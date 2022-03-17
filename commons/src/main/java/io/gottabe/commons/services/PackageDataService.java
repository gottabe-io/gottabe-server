package io.gottabe.commons.services;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.repositories.PackageDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PackageDataService extends AbstractCrudService<PackageData, Long> {

    @Autowired
    public PackageDataService(PackageDataRepository repository) {
        super(repository);
    }

    protected PackageDataRepository getRepository() {
        return (PackageDataRepository) this.repository;
    }

    public Page<PackageData> findByGroup(String groupName, int page, int size) {
        return getRepository().findByGroupName(groupName, PageRequest.of(page, size));
    }

    public Optional<PackageData> findByGroupAndName(String groupName, String packageName) {
        return getRepository().findByGroupNameAndName(groupName, packageName);
    }

    public PackageData getOrCreate(PackageGroup group, String packageName) {
        return getRepository().findByGroupNameAndName(group.getName(), packageName).orElse(create(group, packageName));
    }

    private PackageData create(PackageGroup group, String packageName) {
        return save(PackageData.builder().group(group).name(packageName).build());
    }
}
