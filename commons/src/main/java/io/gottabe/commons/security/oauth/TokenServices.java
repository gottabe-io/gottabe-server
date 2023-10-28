package io.gottabe.commons.security.oauth;

import io.gottabe.commons.exceptions.AnyAuthenticationException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;

@Service
public class TokenServices {

    private static final String TOKEN_TYPE = "Bearer";

    private TokenStore tokenStore;

    private PasswordEncoder passwordEncoder;

    private UserDetailsService userDetailsService;

    private final RandomUuidGenerator randomUuidGenerator = new RandomUuidGenerator();

    public Authentication getUserAuthentication(Oauth2AuthenticationRequest request) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        } catch(UsernameNotFoundException e) {
            throw new AnyAuthenticationException("authentication.user.not_found");
        } catch (DisabledException e) {
            throw new AnyAuthenticationException("authentication.user.disabled");
        }
        if (!userDetails.isAccountNonExpired())
            throw new AnyAuthenticationException("authentication.user.expired");
        if (!userDetails.isAccountNonLocked())
            throw new AnyAuthenticationException("authentication.user.locked");
        if (!userDetails.isCredentialsNonExpired())
            throw new AnyAuthenticationException("authentication.user.credentials.expired");
        if (!userDetails.isEnabled())
            throw new AnyAuthenticationException("authentication.user.disabled");
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
            throw new AnyAuthenticationException("authentication.user.not_found");
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public Authentication getRefreshTokenAuthentication(Oauth2AuthenticationRequest request) {
        Oauth2RefreshToken refreshToken = tokenStore.findRefreshToken(request.getRefreshToken());
        if (refreshToken == null)
            throw new AnyAuthenticationException("authentication.refresh.not_found");
        if (refreshToken.getExpires() != null && new Date().after(refreshToken.getExpires()))
            throw new AnyAuthenticationException("authentication.refresh.expired");
        Oauth2AccessToken oldAccess = tokenStore.findAccessToken(refreshToken.getTokenId());
        if (oldAccess == null) {
            tokenStore.removeRefreshToken(request.getRefreshToken());
            throw new AnyAuthenticationException("authentication.refresh.failed");
        }
        try {
            return deserializeTokenAuthentication(Base64.decodeBase64(oldAccess.getAuthentication()));
        } catch (IOException e) {
            throw new AnyAuthenticationException("authentication.refresh.failed");
        } finally {
            tokenStore.removeAccessToken(oldAccess.getTokenId());
            tokenStore.removeRefreshToken(request.getRefreshToken());
        }
    }

    public String extractToken(HttpServletRequest request) {
        final String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.toLowerCase().startsWith(TOKEN_TYPE.toLowerCase() + " "))
            return null;
        return auth.substring(TOKEN_TYPE.length() + 1).trim();
    }

    public Authentication getAuthentication(HttpServletRequest request) throws IOException {
        final String tokenId = extractToken(request);
        if (tokenId != null && !tokenId.isEmpty()) {
            Oauth2AccessToken token = tokenStore.findAccessToken(tokenId);
            if (token != null) {
                if (token.getExpires() != null && token.getExpires().before(new Date())) {
                    return null;
                }
                return deserializeTokenAuthentication(Base64.decodeBase64(token.getAuthentication()));
            }
        }
        return null;
    }

    private Authentication deserializeTokenAuthentication(byte[] bytes) throws IOException {
        try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (Authentication) is.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error reading authentication", e);
        }
    }

    public OAuth2Token createToken(Authentication authentication, Oauth2TokenRequest request, ClientDetails clientDetails) throws IOException {
        Oauth2AccessToken tokenAuth = new Oauth2AccessToken();
        Oauth2RefreshToken refreshToken = null;
                tokenAuth.setTokenId(randomUuidGenerator.next());
        tokenAuth.setClientId(clientDetails.getClientId());
        tokenAuth.setUsername(authentication.getName());
        tokenAuth.setCreation(new Date());
        if (clientDetails.getAccessTokenValiditySeconds() > 0)
            tokenAuth.setExpires(new Date(System.currentTimeMillis() + clientDetails.getAccessTokenValiditySeconds() * 1000L));
        else
            tokenAuth.setExpires(null);
        if (clientDetails.getAccessTokenValiditySeconds() > 0 && clientDetails.getRefreshTokenValiditySeconds() > 0) {
            refreshToken = createRefreshToken(clientDetails, tokenAuth);
            tokenAuth.setRefreshId(refreshToken.getRefreshId());
        }
        tokenAuth.setAuthentication(Base64.encodeBase64String(serializeAuthentication(authentication)));
        tokenStore.storeAccessToken(tokenAuth);
        if (refreshToken != null)
            tokenStore.storeRefreshToken(refreshToken);
        return OAuth2Token.builder()
                .token(tokenAuth.getTokenId())
                .tokenType(TOKEN_TYPE)
                .refreshToken(tokenAuth.getRefreshId())
                .expiresIn(tokenAuth.getExpires())
                .scope(request.getGrantTypes().stream().reduce((a,b) -> a + " " + b).orElse(""))
                .build();
    }

    private byte[] serializeAuthentication(Authentication authentication) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(authentication);
        }
        return bos.toByteArray();
    }

    private Oauth2RefreshToken createRefreshToken(ClientDetails clientDetails, Oauth2AccessToken tokenAuth) {
        Oauth2RefreshToken refreshToken = new Oauth2RefreshToken();
        refreshToken.setTokenId(tokenAuth.getTokenId());
        refreshToken.setRefreshId(randomUuidGenerator.next());
        refreshToken.setExpires(new Date(System.currentTimeMillis() + clientDetails.getRefreshTokenValiditySeconds() * 1000L));
        refreshToken.setClientId(clientDetails.getClientId());
        refreshToken.setUsername(tokenAuth.getUsername());
        return refreshToken;
    }

    public void revokeToken(String tokenId) {
        Oauth2AccessToken token = tokenStore.findAccessToken(tokenId);
        if (token == null) throw new ResourceNotFoundException();
        tokenStore.removeAccessToken(token);
        tokenStore.removeRefreshToken(token.getRefreshId());
    }

    @Autowired
    public void setTokenStore(@Lazy TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserDetailsService(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}