package io.gottabe.commons.vo.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TargetConfig {
    private String name;
    private String arch;
    private String platform;
    private String toolchain;
    private List<PluginConfig> plugins;
    private List<String> includeDirs;
    private List<String> sources;
    private Options options;
    private Map<String, String> defines;
    private List<String> libraryPaths;
    private List<String> libraries;
    private LinkOptions  linkOptions;
}
