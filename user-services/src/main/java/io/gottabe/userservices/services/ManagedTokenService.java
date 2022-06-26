package io.gottabe.userservices.services;

import io.gottabe.commons.entities.User;
import io.gottabe.commons.security.oauth.*;
import io.gottabe.commons.services.UserService;
import io.gottabe.commons.vo.ManagedTokenVO;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManagedTokenService {

    @Value("${gottabeio.cli.client_id}")
    private String clientId;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenServices tokenServices;

    @Autowired
    private ClientDetailsService clientDetailsService;

    public List<ManagedTokenVO> findByUser(User user) {
        return tokenStore.findAccessTokensByUsername(user.getEmail()).stream()
                .filter(tok -> tok.getClientId().equals(clientId))
                .map(this::decodeToken)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private ManagedTokenVO decodeToken(Oauth2AccessToken token) {
        return new ManagedTokenVO(token.getTokenId(), token.getExpires(), token.getCreation());
    }

    public void revokeToken(String tokenId) {
        Oauth2AccessToken token = tokenStore.findAccessToken(tokenId);
        tokenStore.removeAccessToken(token);
    }

    public ManagedTokenVO createToken(User user) throws IOException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), Set.of(new SimpleGrantedAuthority("ROLE_CLI")));
        Oauth2TokenRequest request = Oauth2TokenRequest.builder()
                .clientId(clientId)
                .grantTypes(Set.of("cli"))
                .build();
        OAuth2Token token = tokenServices.createToken(authentication, request, clientDetailsService.loadClientByClientId(clientId));
        return new ManagedTokenVO(token.getToken(), token.getExpiresIn(), new Date());
    }
}
