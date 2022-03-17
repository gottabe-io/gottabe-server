package io.gottabe.commons.vo;

import io.gottabe.commons.enums.OrgUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgUserVO {

    private String name;

    private String nickname;

    private OrgUserRole role;

    private boolean active;

    private LocalDateTime inviteDate;

}
