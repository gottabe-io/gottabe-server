package io.gottabe.commons.services;

import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.repositories.PackageGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PackageGroupService extends AbstractCrudService<PackageGroup, Long> {

    @Autowired
    public PackageGroupService(PackageGroupRepository repository) {
        super(repository);
    }

    protected PackageGroupRepository getRepository() {
        return (PackageGroupRepository) this.repository;
    }

    public Optional<PackageGroup> findByGroupName(String packageName) {
        return getRepository().findByName(packageName);
    }

    public Page<PackageGroup> findByGroupNameLike(String groupName, int page, int size) {
        return getRepository().findByNameStartsWith(groupName, PageRequest.of(page, size));
    }
}
