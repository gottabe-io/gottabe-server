package io.gottabe.commons.security.oauth;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("Oauth2AccessToken:ids")
public class Oauth2AccessToken {

    @Id
    private String tokenId;
    private String refreshId;
    private String clientId;
    private String username;
    private Date expires;
    private Date creation;
    private String authentication;

}
