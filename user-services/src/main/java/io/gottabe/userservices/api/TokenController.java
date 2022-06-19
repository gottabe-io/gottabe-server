package io.gottabe.userservices.api;

import io.gottabe.commons.exceptions.AnyAuthenticationException;
import io.gottabe.commons.security.oauth.*;
import io.gottabe.userservices.services.GottabeClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("oauth")
public class TokenController {

    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String GRANT_TYPE = "grant_type";
    public static final String BASIC_ = "basic ";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private TokenServices tokenServices;

    @GetMapping("token")
    public ResponseEntity<OAuth2Token> getAuthenticate(@RequestParam Map<String, String> parameters,
                                                       HttpServletRequest httpRequest) throws IOException {
        return authenticate(parameters, httpRequest);
    }

    @PostMapping("token")
    public ResponseEntity<OAuth2Token> authenticate(@RequestParam Map<String, String> parameters,
                                                   HttpServletRequest httpRequest) throws IOException {
        Oauth2AuthenticationRequest request = getOauth2AuthenticationRequest(parameters, httpRequest);
        GottabeClientDetailsService.ClientDetailsImpl clientDetails = (GottabeClientDetailsService.ClientDetailsImpl) clientDetailsService.loadClientByClientId(request.getClientId());
        if (clientDetails == null)
            throw new AnyAuthenticationException("authentication.client.failed");
        if (clientDetails.isSecretRequired() && !clientDetails.getClientSecret().equals(request.getClientSecret()))
            throw new AnyAuthenticationException("authentication.client.failed");
        if (!clientDetails.getAuthorizedGrantTypes().contains(request.getGrantType()))
            throw new AnyAuthenticationException("authentication.client.grant_type");
        if (!clientDetails.getAutoApproveScopes().isEmpty())
            throw new AnyAuthenticationException("authentication.client.failed");
        if (request.getGrantType().equals("implicit"))
            throw new AnyAuthenticationException("authentication.client.grant_type");
        Authentication authentication;
        if (request.getGrantType().equals(REFRESH_TOKEN)) {
            authentication = tokenServices.getRefreshTokenAuthentication(request);
        } else {
            authentication = tokenServices.getUserAuthentication(request);
        }
        OAuth2Token token = tokenServices.createToken(authentication, Oauth2TokenRequest.builder()
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .grantTypes(Set.of(request.getGrantType()))
                .build(), clientDetails);
        return ResponseEntity.ok(token);
    }

    private Oauth2AuthenticationRequest getOauth2AuthenticationRequest(Map<String, String> parameters, HttpServletRequest httpRequest) {
        String clientId;
        String clientSecret;
        if (parameters.containsKey(CLIENT_ID)) {
            clientId = parameters.get(CLIENT_ID);
            clientSecret = parameters.get(CLIENT_SECRET);
        } else {
            String headerAuthorization = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (headerAuthorization == null || !headerAuthorization.toLowerCase().startsWith(BASIC_))
                throw new AnyAuthenticationException("authentication.client.failed");
            String clientIdAndSecret = new String(Base64.getDecoder().decode(headerAuthorization.substring(BASIC_.length()).trim()));
            String[] strs = clientIdAndSecret.split("[:]");
            if (strs.length != 2)
                throw new AnyAuthenticationException("authentication.client.failed");
            clientId = strs[0];
            clientSecret = strs[1];
        }
        return Oauth2AuthenticationRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .refreshToken(parameters.get(REFRESH_TOKEN))
                .username(parameters.get(USERNAME))
                .password(parameters.get(PASSWORD))
                .grantType(parameters.get(GRANT_TYPE))
                .build();
    }

    @PostMapping("revoke/{tokenId}")
    public ResponseEntity<Void> revokeToken(@PathVariable("tokenId") String tokenId, @RequestParam Map<String, String> parameters) throws IOException {
        tokenServices.revokeToken(tokenId);
        return ResponseEntity.ok().build();
    }

}
