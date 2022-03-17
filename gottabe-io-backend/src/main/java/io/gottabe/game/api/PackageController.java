package io.gottabe.game.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageFile;
import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.exceptions.GottabeException;
import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.PackageDataMapper;
import io.gottabe.commons.mapper.PackageGroupMapper;
import io.gottabe.commons.services.*;
import io.gottabe.commons.util.Messages;
import io.gottabe.commons.vo.PackageDataVO;
import io.gottabe.commons.vo.PackageGroupVO;
import io.gottabe.commons.vo.PackageReleaseVO;
import io.gottabe.commons.vo.build.BuildDescriptor;
import io.gottabe.commons.vo.build.TargetConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/packages")
public class PackageController {

    @Autowired
    private PackageGroupService packageGroupService;

    @Autowired
    private PackageDataService packageDataService;

    @Autowired
    private PackageReleaseService packageReleaseService;

    @Autowired
    private PackageFileService packageFileService;

    @Autowired
    private UserService userService;

    @Autowired
    private Messages messages;

    @GetMapping("all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PackageGroupVO>> groups(@RequestParam("groupName") String groupName,
                                                       @RequestParam(value = "page", required = false) Integer page,
                                                       @RequestParam(value = "size", required = false) Integer size) {
        Page<PackageGroup> results = packageGroupService.findByGroupNameLike(groupName,
                page != null ? page : 0, size != null ? size : 25);
        List<PackageGroupVO> groups = results.stream()
                .map(PackageGroupMapper.INSTANCE::groupToVO)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("RESULT_COUNT", String.valueOf(results.getTotalElements()))
                .body(groups);
    }

    @GetMapping("{groupName}")
    @Transactional(readOnly = true)
    public ResponseEntity<PackageGroupVO> groupInfo(@PathVariable("groupName") String groupName) {
        PackageGroupVO packages = packageGroupService.findByGroupName(groupName)
                .map(PackageGroupMapper.INSTANCE::groupToVO)
                .orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("{groupName}/all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PackageDataVO>> groupPackages(@PathVariable("groupName") String groupName,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size) {
        Page<PackageData> pages = packageDataService.findByGroup(groupName,
                page != null ? page : 0, size != null ? size : 25);
        List<PackageDataVO> packages = pages.stream()
                .map(PackageDataMapper.INSTANCE::packageToVO)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("RESULT_COUNT", String.valueOf(pages.getTotalElements()))
                .body(packages);
    }

    @GetMapping("{groupName}/{packageName}")
    @Transactional(readOnly = true)
    public ResponseEntity<PackageDataVO> packageInfo(@PathVariable("groupName") String groupName, @PathVariable("packageName") String packageName) {
        PackageDataVO packages = packageDataService.findByGroupAndName(groupName, packageName)
                .map(PackageDataMapper.INSTANCE::packageToVO)
                .orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("{groupName}/{packageName}/{version}")
    @Transactional(readOnly = true)
    public ResponseEntity<PackageReleaseVO> packageInfo(@PathVariable("groupName") String groupName, @PathVariable("packageName") String packageName, @PathVariable("version") String version) {
        PackageReleaseVO packageRelease = packageReleaseService.findByGroupAndNameAndVersionVO(groupName, packageName, version);
        return ResponseEntity.ok(packageRelease);
    }

    @PostMapping("{groupName}/{packageName}/{version}/build.json")
    @Transactional
    public ResponseEntity<Void> createPackage(@PathVariable("groupName") String groupName,
                                              @PathVariable("packageName") String packageName,
                                              @PathVariable("version") String version,
                                              @Valid @RequestBody BuildDescriptor build) throws IOException {
        if (packageReleaseService.findByGroupAndNameAndVersion(groupName, packageName, version).isPresent()) {
            throw new InvalidRequestException("package.version.already.exists");
        }
        PackageGroup group = packageGroupService.findByGroupName(groupName).orElseThrow(() -> new InvalidRequestException(messages.format("group.invalid")));
        PackageData packageData = packageDataService.getOrCreate(group, packageName);

        PackageRelease release = packageReleaseService.createRelease(version, build, packageData);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        new ObjectMapper().writeValue(bos, build);
        InputStream bis = new ByteArrayInputStream(bos.toByteArray());
        packageFileService.saveFile(release, "build.json", bis);

        getFileNames(build).forEach(filename -> {
            try {
                packageFileService.saveFile(release, filename, null);
            } catch (IOException e) {
                throw new GottabeException(e);
            }
        });

        return ResponseEntity.status(201).build();
    }

    private List<String> getFileNames(BuildDescriptor build) {
        List<String> result = new ArrayList<>();
        if (build.getTargets() != null)
            build.getTargets().stream().map(target -> mapTarget(target, build)).forEach(result::add);
        if (build.getPackageConfig() != null && build.getPackageConfig().getIncludes() != null && !build.getPackageConfig().getIncludes().isEmpty())
            result.add("includes.zip");
        if (build.getPackageConfig() != null && build.getPackageConfig().getOther() != null && !build.getPackageConfig().getOther().isEmpty())
            result.add("other.zip");
        return result;
    }

    private String mapTarget(TargetConfig target, BuildDescriptor build) {
        return build.getArtifactId() + target.getArch() + '_' + target.getPlatform() + '_' + target.getToolchain() + ".zip";
    }

    @PostMapping("{groupName}/{packageName}/{version}")
    @Transactional
    public ResponseEntity<Void> postPackageFile(@PathVariable("groupName") String groupName,
                                                @PathVariable("packageName") String packageName,
                                                @PathVariable("version") String version,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null) {
            throw new InvalidRequestException("package.file.not.selected");
        }

        InputStream inputStream = file.getInputStream();
        String name = file.getName();
        long size = file.getSize();
        if (size > 157_286_400)
            throw new InvalidRequestException(messages.format("package.maxSize.exceeded", 157_286_400));

        PackageFile packageFile = packageFileService.findByGroupPackageVersionAndName(groupName, packageName, version, name)
                .orElseThrow(ResourceNotFoundException::new);

        packageFileService.saveFile(packageFile, inputStream);

        return ResponseEntity.status(200).build();
    }

    @GetMapping("{groupName}/{packageName}/{version}/{filename}")
    @Transactional
    public ResponseEntity<Resource> getPackageFile(@PathVariable("groupName") String groupName,
                                                             @PathVariable("packageName") String packageName,
                                                             @PathVariable("version") String version,
                                                             @PathVariable("filename") String filename) throws IOException {

        PackageFile packageFile = packageFileService.findByGroupPackageVersionAndName(groupName, packageName, version, filename)
                .orElseThrow(ResourceNotFoundException::new);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Optional<Resource> resource = packageFileService.loadFile(packageFile);

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(packageFile.getLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource.orElseThrow(ResourceNotFoundException::new));
    }

}
