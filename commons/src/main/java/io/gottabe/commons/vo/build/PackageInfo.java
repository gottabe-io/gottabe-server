package io.gottabe.commons.vo.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageInfo extends BaseDescriptor {
    private String includeDir;
    private BuildDescriptor build;
    private String checksum;
    private List<PackageInfo> dependencies;
    private List<String> scope;
}
