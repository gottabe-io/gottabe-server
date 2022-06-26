package io.gottabe.commons.vo;

import io.gottabe.commons.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageDataVO {

    @NotNull
    @Pattern(regexp = "^([a-z][a-z0-9_-]+)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "invalid.package.name")
    private String name;

    @NotNull
    private PackageType type;

    private boolean isPublic;

    private PackageGroupVO group;

    private List<PackageReleaseVO> releases;

}
