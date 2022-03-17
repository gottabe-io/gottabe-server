package io.gottabe.commons.vo.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PluginDescriptor extends BaseDescriptor {
    private String main;
    private List<String> phases;
}


