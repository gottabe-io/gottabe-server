package io.gottabe.userservices.config;

import io.gottabe.userservices.services.GottabeClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    AuthenticationManager authenticationManager;

    @Autowired
    GottabeClientDetailsService gottabeClientDetailsService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    DataSource dataSource;

    public AuthorizationServerConfig(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {

        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.withClientDetails(gottabeClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .userDetailsService(userDetailsService)
                .authenticationManager(this.authenticationManager)
                .tokenStore(tokenStore());

    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

}

/**
 * Legacy Authorization Server (spring-security-oauth2) does not support any
 * Token Introspection endpoint.
 *
 * This class adds ad-hoc support in order to better support the other samples in the repo.
 */
@FrameworkEndpoint
class IntrospectEndpoint {
    TokenStore tokenStore;

    IntrospectEndpoint(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @PostMapping("/introspect")
    @ResponseBody
    public Map<String, Object> introspect(@RequestParam("token") String token) {
        OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(token);
        Map<String, Object> attributes = new HashMap<>();
        if (accessToken == null || accessToken.isExpired()) {
            attributes.put("active", false);
            return attributes;
        }

        OAuth2Authentication authentication = this.tokenStore.readAuthentication(token);

        attributes.put("active", true);
        attributes.put("exp", accessToken.getExpiration().getTime());
        attributes.put("scope", accessToken.getScope().stream().collect(Collectors.joining(" ")));
        attributes.put("sub", authentication.getName());

        return attributes;
    }
}


@FrameworkEndpoint
class RevokeEndpoint {

    private final TokenStore tokenStore;

    RevokeEndpoint(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @PostMapping("/oauth/revoke/{tokenId:.*}")
    @ResponseBody
    public void revoke(@PathVariable("tokenId") String tokenId) {
        OAuth2RefreshToken token = new DefaultOAuth2RefreshToken(tokenId);
        tokenStore.removeRefreshToken(token);
        tokenStore.removeAccessTokenUsingRefreshToken(token);
    }
}
