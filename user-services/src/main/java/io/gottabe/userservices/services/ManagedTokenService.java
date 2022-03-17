package io.gottabe.userservices.services;

import io.gottabe.commons.entities.User;
import io.gottabe.commons.services.UserService;
import io.gottabe.commons.vo.ManagedTokenVO;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class ManagedTokenService {

    private static final String QUERY_TOKENS = "select token from oauth_access_token where user_name = ? and client_id = ?";

    @Value("${gottabeio.cli.client_id}")
    private String clientId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationServerTokenServices tokenServices;

    public List<ManagedTokenVO> findByUser(User user) {
        PreparedStatementSetter pss = (PreparedStatement stmt) -> {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, clientId);
        };
        return jdbcTemplate.query(QUERY_TOKENS, pss, (ResultSet rs, int row) -> decodeToken(rs.getBytes(1)));
    }

    @SneakyThrows
    private ManagedTokenVO decodeToken(byte[] bytes) {
        ObjectInputStream dis = new ObjectInputStream(new ByteArrayInputStream(bytes));
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) dis.readObject();
        return new ManagedTokenVO(token.getValue(), token.getExpiration());
    }

    public void revokeToken(String tokenId) {
        OAuth2AccessToken token = new DefaultOAuth2AccessToken(tokenId);
        tokenStore.removeAccessToken(token);
    }

    public ManagedTokenVO createToken(User user) {
        OAuth2Request request = new OAuth2Request(new HashMap<>(), clientId, Set.of(), true, Set.of("cli"), null, null, Set.of(), null);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), Set.of(new SimpleGrantedAuthority("cli")));
        OAuth2Authentication oauth = new OAuth2Authentication(request, authentication);
        OAuth2AccessToken token = tokenServices.createAccessToken(oauth);
        return new ManagedTokenVO(token.getValue(), token.getExpiration());
    }
}
