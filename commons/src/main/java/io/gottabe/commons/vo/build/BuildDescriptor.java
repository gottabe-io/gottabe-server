package io.gottabe.commons.vo.build;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuildDescriptor extends BaseDescriptor {

    @NotNull
    @NotEmpty
    private String type;
    private List<BuildDescriptor> modules;
    private List<PluginConfig> plugins;
    private List<String> dependencies;
    private List<String> includeDirs;
    private List<String> sources;
    private List<String> testSources;
    private List<TargetConfig> targets;
    @JsonProperty("package")
    private PackageConfig packageConfig;
}
