package io.gottabe.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVO {

    private Long id;

    private AnyUserVO user;

    private int rate;

    private String title;

    private String review;

    private boolean vulnerability;

    private int reviewLikes;

    private int reviewDislikes;

    private int myRate;

}
