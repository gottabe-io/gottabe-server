package io.gottabe.game.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gottabe.commons.entities.*;
import io.gottabe.commons.enums.OrgUserRole;
import io.gottabe.commons.enums.PackageType;
import io.gottabe.commons.enums.ReleaseFetchType;
import io.gottabe.commons.exceptions.AccessDeniedException;
import io.gottabe.commons.exceptions.GottabeException;
import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.PackageDataMapper;
import io.gottabe.commons.mapper.PackageGroupMapper;
import io.gottabe.commons.mapper.PackageReleaseMapper;
import io.gottabe.commons.services.*;
import io.gottabe.commons.util.IOUtils;
import io.gottabe.commons.util.Messages;
import io.gottabe.commons.vo.PackageDataVO;
import io.gottabe.commons.vo.PackageGroupVO;
import io.gottabe.commons.vo.PackageReleaseVO;
import io.gottabe.commons.vo.build.BuildDescriptor;
import io.gottabe.commons.vo.build.PluginDescriptor;
import io.gottabe.commons.vo.build.TargetConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("mine")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PackageDataVO>> myPackages(@RequestParam(value = "type", defaultValue = "PACKAGE,PLUGIN") Set<PackageType> types,
                                                          @RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "size", required = false) Integer size) {
        Page<PackageData> pages = packageDataService.findByOwnerAndTypes(userService.currentUser(), types, Set.of(Boolean.FALSE, Boolean.TRUE),
                page != null ? page : 0, size != null ? size : 25);
        List<PackageDataVO> packages = pages.stream()
                .map(PackageDataMapper.INSTANCE::packageToVOWithReleases)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("RESULT_COUNT", String.valueOf(pages.getTotalElements()))
                .body(packages);
    }

    @GetMapping("from")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PackageDataVO>> packagesFrom(@RequestParam(value = "type", defaultValue = "PACKAGE,PLUGIN") Set<PackageType> types,
                                                            @RequestParam(value = "owner") String nickname,
                                                            @RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "size", required = false) Integer size) {
        BaseOwner owner = userService.findOwnerByNickname(nickname);
        User currentUser = userService.currentUser();
        Set<Boolean> visibility = new HashSet<>(Set.of(Boolean.FALSE));
        if (owner instanceof Organization) {
            Organization org = (Organization) owner;
            if (org.getUsers().stream().anyMatch(orgUser -> orgUser.getUser().equals(currentUser))) {
                visibility.add(Boolean.TRUE);
            }
        } else {
            User user = (User) owner;
            if (user.equals(currentUser))
                visibility.add(Boolean.TRUE);
        }
        Page<PackageData> pages = packageDataService.findByOwnerAndTypes(owner, types, visibility,
                page != null ? page : 0, size != null ? size : 25);
        List<PackageDataVO> packages = pages.stream()
                .map(PackageDataMapper.INSTANCE::packageToVOWithReleases)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("RESULT_COUNT", String.valueOf(pages.getTotalElements()))
                .body(packages);
    }

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
                                                             @RequestParam(value = "type", defaultValue = "PACKAGE") PackageType type,
                                                             @RequestParam(value = "fetch", required = false) ReleaseFetchType fetch,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size) {
        Page<PackageData> pages = packageDataService.findByGroupLike(groupName + "%", type,
                page != null ? page : 0, size != null ? size : 25);
        List<PackageDataVO> packages = pages.stream()
                .map(pack -> {
                    PackageDataVO vo = PackageDataMapper.INSTANCE.packageToVO(pack);
                    if (ReleaseFetchType.LATEST_RELEASE.equals(fetch))
                        vo.setReleases(packageReleaseService.findOneByPackageDataOrderByReleaseDateDesc(pack).stream()
                                .map(PackageReleaseMapper.INSTANCE::releaseToVO)
                                .collect(Collectors.toList()));
                    return vo;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("RESULT_COUNT", String.valueOf(pages.getTotalElements()))
                .body(packages);
    }

    @GetMapping("{groupName}/{packageName}")
    @Transactional(readOnly = true)
    public ResponseEntity<PackageDataVO> packageInfo(@PathVariable("groupName") String groupName,
                                                     @PathVariable("packageName") String packageName,
                                                     @RequestParam(value = "type", defaultValue = "PACKAGE,PLUGIN") Set<PackageType> types,
                                                     @RequestParam(value = "fetch", required = false) ReleaseFetchType fetch) {
        PackageDataVO packages = packageDataService.findByGroupAndName(groupName, packageName, types)
                .map(pack -> {
                    PackageDataVO vo = PackageDataMapper.INSTANCE.packageToVO(pack);
                    if (ReleaseFetchType.LATEST_RELEASE.equals(fetch))
                        vo.setReleases(packageReleaseService.findOneByPackageDataOrderByReleaseDateDesc(pack).stream()
                                .map(PackageReleaseMapper.INSTANCE::releaseToVO)
                                .collect(Collectors.toList()));
                    if (ReleaseFetchType.ALL_RELEASES.equals(fetch))
                        vo.setReleases(pack.getReleases().stream()
                                .map(PackageReleaseMapper.INSTANCE::releaseToVO)
                                .collect(Collectors.toList()));
                    return vo;
                })
                .orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("{groupName}/{packageName}/{version}")
    @Transactional(readOnly = true)
    public ResponseEntity<PackageReleaseVO> packageReleases(@PathVariable("groupName") String groupName, @PathVariable("packageName") String packageName, @PathVariable("version") String version,
                                                            @RequestParam(value = "type", defaultValue = "PACKAGE,PLUGIN") Set<PackageType> types) {
        PackageReleaseVO packageRelease = packageReleaseService.findByGroupAndNameAndVersionVO(groupName, packageName, version, types);
        return ResponseEntity.ok(packageRelease);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    @Transactional
    public ResponseEntity<Void> createGroup(@Valid @RequestBody PackageGroupVO groupVo) throws IOException {
        if (packageGroupService.findByGroupName(groupVo.getName()).isPresent()) {
            throw new InvalidRequestException("group.already.exists");
        }
        PackageGroup group = PackageGroup.builder()
                .name(groupVo.getName())
                .owner(userService.currentUser())
                .description(groupVo.getDescription())
                .build();
        packageGroupService.save(group);
        return ResponseEntity.status(201).build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("{groupName}")
    @Transactional
    public ResponseEntity<Void> createPackage(@PathVariable("groupName") String groupName,
                                              @Valid @RequestBody PackageDataVO dataVo) throws IOException {
        PackageGroup group = packageGroupService.findByGroupName(groupName).orElseThrow(() -> new InvalidRequestException(messages.format("group.invalid")));
        checkWritePermission(group.getOwner());
        if (packageDataService.existsByGroupAndName(groupName, dataVo.getName())) {
            throw new InvalidRequestException("package.already.exists");
        }
        PackageData data = PackageData.builder()
                .group(group)
                .name(dataVo.getName())
                .type(dataVo.getType())
                .build();
        packageDataService.save(data);
        return ResponseEntity.status(201).build();
    }

    @PreAuthorize("hasAuthority('ROLE_CLI')")
    @PostMapping("{groupName}/{packageName}/{version}/build.json")
    @Transactional
    public ResponseEntity<Void> createPackage(@PathVariable("groupName") String groupName,
                                              @PathVariable("packageName") String packageName,
                                              @PathVariable("version") String version,
                                              @Valid @RequestBody BuildDescriptor build) throws IOException {
        PackageData packageData = getPackageData(groupName, packageName, version, PackageType.PACKAGE);

        PackageRelease release = packageReleaseService.createRelease(version, build, packageData);

        createPackageFiles(build, release, getFileNames(build), "build.json");

        return ResponseEntity.status(201).build();
    }

    @PreAuthorize("hasAuthority('ROLE_CLI')")
    @PostMapping("{groupName}/{packageName}/{version}/plugin.json")
    @Transactional
    public ResponseEntity<Void> createPlugin(@PathVariable("groupName") String groupName,
                                             @PathVariable("packageName") String packageName,
                                             @PathVariable("version") String version,
                                             @Valid @RequestBody PluginDescriptor plugin) throws IOException {
        PackageData packageData = getPackageData(groupName, packageName, version, PackageType.PLUGIN);

        PackageRelease release = packageReleaseService.createRelease(version, plugin, packageData);

        createPackageFiles(plugin, release, getFileNames(plugin), "plugin.json");

        return ResponseEntity.status(201).build();
    }

    private PackageData getPackageData(String groupName, String packageName, String version, PackageType type) {
        if (packageReleaseService.findByGroupAndNameAndVersion(groupName, packageName, version, Set.of(type)).isPresent()) {
            throw new InvalidRequestException("package.version.already.exists");
        }
        PackageGroup group = packageGroupService.findByGroupName(groupName).orElseThrow(() -> new InvalidRequestException(messages.format("group.invalid")));
        checkWritePermission(group.getOwner());
        return packageDataService.getOrCreate(group, packageName, type);
    }

    private <T> void createPackageFiles(T descriptor, PackageRelease release, List<String> fileNames, String descriptorName) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        new ObjectMapper().writeValue(bos, descriptor);
        InputStream bis = new ByteArrayInputStream(bos.toByteArray());
        packageFileService.saveFile(release, descriptorName, bis);

        fileNames.forEach(filename -> {
            try {
                packageFileService.saveFile(release, filename, null);
            } catch (IOException e) {
                throw new GottabeException(e);
            }
        });
    }

    private void checkWritePermission(BaseOwner owner) {
        if (owner instanceof User) {
            if (userService.currentUser().equals(owner))
                return;
        } else if (owner instanceof Organization) {
            Organization org = (Organization) owner;
            if (org.getUsers().stream()
                    .anyMatch(orgUser -> orgUser.getUser().equals(userService.currentUser())
                            && orgUser.getRole().ordinal() >= OrgUserRole.DEVELOPER.ordinal()))
                return;
        }
        throw new AccessDeniedException();
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

    private List<String> getFileNames(PluginDescriptor plugin) {
        List<String> result = new ArrayList<>();
        result.add(plugin.getArtifactId() + "_" + plugin.getVersion() + ".zip");
        return result;
    }

    @PreAuthorize("hasAuthority('ROLE_CLI')")
    @PostMapping("{groupName}/{packageName}/{version}/{filename}")
    @Transactional
    public ResponseEntity<Void> postPackageFile(@PathVariable("groupName") String groupName,
                                                @PathVariable("packageName") String packageName,
                                                @PathVariable("version") String version,
                                                @PathVariable("filename") String filename,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null) {
            throw new InvalidRequestException("package.file.not.selected");
        }

        InputStream inputStream = file.getInputStream();
        long size = file.getSize();
        if (size > 157_286_400)
            throw new InvalidRequestException(messages.format("package.maxSize.exceeded", 157_286_400));

        PackageGroup group = packageGroupService.findByGroupName(groupName).orElseThrow(() -> new InvalidRequestException(messages.format("group.invalid")));
        checkWritePermission(group.getOwner());

        PackageType type = packageDataService.findOneByGroupNameAndName(groupName, packageName).orElseThrow(ResourceNotFoundException::new).getType();

        PackageFile packageFile = packageFileService.findByGroupPackageVersionAndName(groupName, packageName, version, filename, Set.of(type))
                .orElseGet(() -> {
                    if (filename.equals("README.md") || filename.equals("LICENSE"))
                        try {
                            PackageRelease release = packageReleaseService.findByGroupAndNameAndVersion(groupName, packageName, version, Set.of(type)).orElseThrow(ResourceNotFoundException::new);
                            return packageFileService.saveFile(release, filename, null);
                        } catch (IOException e) {
                            throw new GottabeException(e);
                        };
                    throw new ResourceNotFoundException();
                });

        packageFileService.saveFile(packageFile, inputStream);

        return ResponseEntity.status(200).build();
    }

    @GetMapping("{groupName}/{packageName}/{version}/{filename}")
    @Transactional
    public ResponseEntity<Resource> getPackageFile(@PathVariable("groupName") String groupName,
                                                   @PathVariable("packageName") String packageName,
                                                   @PathVariable("version") String version,
                                                   @PathVariable("filename") String filename,
                                                   @RequestParam(value = "type", defaultValue = "PACKAGE,PLUGIN") String types) throws IOException {

        Set<PackageType> typesSet = Stream.of(types.toUpperCase().split("\\s*,\\s*")).map(PackageType::valueOf).collect(Collectors.toSet());
        PackageFile packageFile = packageFileService.findByGroupPackageVersionAndName(groupName, packageName, version, filename, typesSet)
                .orElseThrow(ResourceNotFoundException::new);

        Optional<Resource> resourceOp = packageFileService.loadFile(packageFile);

        if (packageFile.getUploadDate() == null)
            throw new ResourceNotFoundException("package.file.not.uploaded");

        Resource resource = resourceOp.orElseThrow(() -> new ResourceNotFoundException("package.file.not.uploaded"));

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        header.add(HttpHeaders.CACHE_CONTROL, "private");

        String ext = IOUtils.extractFileExtension(filename);

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(ext.equalsIgnoreCase("json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_OCTET_STREAM)
                .lastModified(packageFile.getUploadDate().toInstant())
                .eTag(version)
                .body(resource);
    }

}
