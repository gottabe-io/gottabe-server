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
public class PackageConfig {
    private String  name;
    private List<String> includes;
    private List<String> other;
}
