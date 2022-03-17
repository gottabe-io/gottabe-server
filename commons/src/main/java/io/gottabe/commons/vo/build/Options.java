package io.gottabe.commons.vo.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Options {
    private Integer optimization;
    private Integer debug;
    private String warnings;
    private String other;
}
