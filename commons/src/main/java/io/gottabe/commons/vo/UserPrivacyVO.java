package io.gottabe.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivacyVO {
    private boolean showEmail;

    private boolean showTwitter;

    private boolean showGithub;

    private boolean showName;
}
