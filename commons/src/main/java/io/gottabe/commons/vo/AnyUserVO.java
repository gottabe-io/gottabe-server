package io.gottabe.commons.vo;

import io.gottabe.commons.enums.OrgUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnyUserVO {

    private String id;

    private String name;

    private String lastName;

    private String email;

    private String nickname;

    private String githubAccount;

    private String twitterAccount;

    private String description;

    private Date createTime;

    private OrgUserRole role;

}
