package io.gottabe.commons.services;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageFile;
import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.enums.IdHash;
import io.gottabe.commons.enums.PackageType;
import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.repositories.PackageFileRepository;
import io.gottabe.commons.store.FileStore;
import io.gottabe.commons.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PackageFileService extends AbstractCrudService<PackageFile, Long> {

    @Autowired
    private FileStore fileStore;

    @Autowired
    public PackageFileService(PackageFileRepository repository) {
        super(repository);
    }

    protected PackageFileRepository getRepository() {
        return (PackageFileRepository) this.repository;
    }

    public Optional<PackageFile> findByGroupPackageVersionAndName(String groupName, String packageName, String version, String name, PackageType type) {
        return getRepository().findByGroupPackageVersionAndNameAndPackageType(groupName, packageName, version, name, type);
    }

    public List<PackageFile> findByGroupPackageAndVersion(String groupName, String packageName, String version) {
        return getRepository().findByGroupPackageAndVersion(groupName, packageName, version);
    }

    public PackageFile saveFile(PackageRelease release, String name, InputStream is) throws IOException {

        PackageData packageData = release.getPackageData();
        PackageGroup group = packageData.getGroup();

        if (is != null) {
            fileStore.putFile("package"+ IdHash.hash(group.getOwner()),
                    IOUtils.concat(group.getName(), packageData.getName(), release.getVersion(), name), is);
        }

        return save(PackageFile.builder().name(name)
                .release(release)
                .uploadDate(is != null ? new Date() : null).build());
    }

    public void saveFile(PackageFile packageFile, InputStream inputStream) throws IOException {
        if (packageFile.getUploadDate() != null) {
            throw new InvalidRequestException("package.file.already.uploaded");
        }
        PackageRelease release = packageFile.getRelease();
        PackageData packageData = release.getPackageData();
        PackageGroup group = packageData.getGroup();

        fileStore.putFile("package"+ IdHash.hash(group.getOwner()),
                IOUtils.concat(group.getName(), packageData.getName(), release.getVersion(), packageFile.getName()), inputStream);
        packageFile.setUploadDate(new Date());
        save(packageFile);
    }

    public Optional<Resource> loadFile(PackageFile packageFile) {
        return fileStore.findFile("package"+ IdHash.hash(packageFile.getRelease().getPackageData().getGroup().getOwner()),
                Stream.of(packageFile.getRelease().getPackageData().getGroup().getName(),
                        packageFile.getRelease().getPackageData().getName(),
                        packageFile.getRelease().getVersion(),
                        packageFile.getName()).reduce((a, b) -> a + '/' + b).get());
    }
}
