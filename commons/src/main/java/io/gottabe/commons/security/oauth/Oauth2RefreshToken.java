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
@RedisHash("Oauth2RefreshToken:ids")
public class Oauth2RefreshToken {

    @Id
    private String refreshId;
    private Date expires;
    private String username;
    private String clientId;
    private String tokenId;

}
