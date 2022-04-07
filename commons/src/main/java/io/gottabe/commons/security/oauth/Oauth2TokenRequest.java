package io.gottabe.commons.security.oauth;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2TokenRequest {

    private Set<String> grantTypes;
    private String clientId;
    private String clientSecret;

}
