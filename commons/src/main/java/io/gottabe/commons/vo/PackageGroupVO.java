package io.gottabe.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageGroupVO {

    @NotNull
    @Pattern(regexp = "^([a-z][a-z0-9_-]+)([.][a-z][a-z0-9_-]+)*$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "invalid.group.name")
    private String name;

    private String description;

    private OwnerVO owner;

}
