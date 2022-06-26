package io.gottabe.commons.vo;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeVO {

    @NotNull
    @Max(1)
    @Min(-1)
    private Integer rate;

}
