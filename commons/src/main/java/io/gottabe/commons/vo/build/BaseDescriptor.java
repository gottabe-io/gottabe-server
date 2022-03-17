package io.gottabe.commons.vo.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseDescriptor {

    @NotNull
    @NotEmpty
    private String groupId;

    @NotNull
    @NotEmpty
    private String artifactId;

    @NotNull
    @NotEmpty
    private String version;
}
