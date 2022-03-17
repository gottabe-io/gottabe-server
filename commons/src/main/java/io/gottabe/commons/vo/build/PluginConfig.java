package io.gottabe.commons.vo.build;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PluginConfig {
    @JsonProperty("package")
	private String packageName;
    private List<String> phases;
    private Map<String, String> config;
}

