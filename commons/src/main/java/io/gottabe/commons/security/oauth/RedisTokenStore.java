package io.gottabe.commons.security.oauth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "gottabeio.store.tokens", havingValue = "redis")
public class RedisTokenStore implements TokenStore {

    public static final String OAUTH_2_ACCESS_TOKEN = "Oauth2AccessToken";
    private static final String OAUTH_2_REFRESH_TOKEN = "Oauth2RefreshToken";

    private RedisTemplate<String, String> redisTemplate;
    private RedisKeyValueTemplate redisKeyValueTemplate;

    public RedisTokenStore(RedisTemplate<String, String> redisTemplate, RedisKeyValueTemplate redisKeyValueTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisKeyValueTemplate = redisKeyValueTemplate;
    }

    @Override
    public Oauth2AccessToken findAccessToken(String tokenId) {
        return redisKeyValueTemplate.findById(tokenId, Oauth2AccessToken.class).orElse(null);
    }

    @Override
    public Oauth2RefreshToken findRefreshToken(String refreshToken) {
        return redisKeyValueTemplate.findById(refreshToken, Oauth2RefreshToken.class).orElse(null);
    }

    @Override
    public void storeAccessToken(Oauth2AccessToken tokenAuth) {
        redisTemplate.opsForSet().add(OAUTH_2_ACCESS_TOKEN + ":all", tokenAuth.getTokenId());
        redisTemplate.opsForSet().add(OAUTH_2_ACCESS_TOKEN + ":username:" + tokenAuth.getUsername(), tokenAuth.getTokenId());
        redisTemplate.opsForSet().add(OAUTH_2_ACCESS_TOKEN + ":client_id:" + tokenAuth.getClientId(), tokenAuth.getTokenId());
        redisKeyValueTemplate.insert(tokenAuth.getTokenId(), tokenAuth);
    }

    @Override
    public void storeRefreshToken(Oauth2RefreshToken refreshToken) {
        redisTemplate.opsForSet().add(OAUTH_2_REFRESH_TOKEN + ":all", refreshToken.getRefreshId());
        redisTemplate.opsForSet().add(OAUTH_2_REFRESH_TOKEN + ":username:" + refreshToken.getUsername(), refreshToken.getRefreshId());
        redisTemplate.opsForSet().add(OAUTH_2_REFRESH_TOKEN + ":client_id:" + refreshToken.getClientId(), refreshToken.getRefreshId());
        redisKeyValueTemplate.insert(refreshToken.getRefreshId(), refreshToken);
    }

    @Override
    public void removeRefreshToken(Oauth2RefreshToken refreshToken) {
        redisTemplate.opsForSet().remove(OAUTH_2_REFRESH_TOKEN + ":all", refreshToken.getRefreshId());
        redisTemplate.opsForSet().remove(OAUTH_2_REFRESH_TOKEN + ":username:" + refreshToken.getUsername(), refreshToken.getRefreshId());
        redisTemplate.opsForSet().remove(OAUTH_2_REFRESH_TOKEN + ":client_id:" + refreshToken.getClientId(), refreshToken.getRefreshId());
        redisKeyValueTemplate.delete(refreshToken);
    }

    @Override
    public void removeAccessToken(Oauth2AccessToken tokenAuth) {
        redisTemplate.opsForSet().remove(OAUTH_2_ACCESS_TOKEN + ":all", tokenAuth.getTokenId());
        redisTemplate.opsForSet().remove(OAUTH_2_ACCESS_TOKEN + ":username:" + tokenAuth.getUsername(), tokenAuth.getTokenId());
        redisTemplate.opsForSet().remove(OAUTH_2_ACCESS_TOKEN + ":client_id:" + tokenAuth.getClientId(), tokenAuth.getTokenId());
        redisKeyValueTemplate.delete(tokenAuth);
    }

    @Override
    public void removeRefreshToken(String refreshId) {
        Oauth2RefreshToken token = findRefreshToken(refreshId);
        removeRefreshToken(token);
    }

    @Override
    public void removeAccessToken(String tokenId) {
        Oauth2AccessToken token = findAccessToken(tokenId);
        removeAccessToken(token);
    }

    @Override
    public List<Oauth2AccessToken> findAccessTokensByUsername(String username) {
        return redisTemplate.opsForSet().members(OAUTH_2_ACCESS_TOKEN + ":username:" + username)
                .stream().map(this::findAccessToken)
                .collect(Collectors.toList());
    }

    @Override
    public List<Oauth2RefreshToken> findRefreshTokensByUsername(String username) {
        return redisTemplate.opsForSet().members(OAUTH_2_REFRESH_TOKEN + ":username:" + username)
                .stream().map(this::findRefreshToken)
                .collect(Collectors.toList());
    }
}
