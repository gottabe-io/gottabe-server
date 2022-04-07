package io.gottabe.commons.security.oauth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2AuthenticationRequest {

    private String username;
    private String password;
    private String grantType;
    private String refreshToken;
    private String clientId;
    private String clientSecret;

}
