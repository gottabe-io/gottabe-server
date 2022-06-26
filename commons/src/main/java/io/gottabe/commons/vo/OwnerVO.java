package io.gottabe.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerVO {

    private String id;

    private String name;

    private String email;

    private String nickname;

    private String image;

    private String githubAccount;

    private String twitterAccount;

    private String description;

    protected Date createTime;
}
