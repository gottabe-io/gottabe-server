package io.gottabe.commons.security.oauth;

import java.util.List;

public interface TokenStore {
    Oauth2AccessToken findAccessToken(String tokenId);

    Oauth2RefreshToken findRefreshToken(String refreshToken);

    void storeAccessToken(Oauth2AccessToken tokenAuth);

    void storeRefreshToken(Oauth2RefreshToken refreshToken);

    void removeRefreshToken(Oauth2RefreshToken refreshToken);

    void removeAccessToken(Oauth2AccessToken token);

    void removeRefreshToken(String refreshId);

    void removeAccessToken(String tokenId);

    List<Oauth2AccessToken> findAccessTokensByUsername(String username);

    List<Oauth2RefreshToken> findRefreshTokensByUsername(String username);

}
