package io.gottabe.commons.security.oauth;

import io.gottabe.commons.security.oauth.Oauth2AccessToken;
import io.gottabe.commons.security.oauth.Oauth2RefreshToken;
import io.gottabe.commons.security.oauth.TokenStore;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@ConditionalOnProperty(name = "gottabeio.store.tokens", havingValue = "jdbc")
public class JdbcTokenStore implements TokenStore {

    private static final String QUERY_ACCESS_TOKEN_BY_ID = "select token_id,\n" +
            "        refresh_id,\n" +
            "        client_id,\n" +
            "        expires_at,\n" +
            "        creation,\n" +
            "        user_name,\n" +
            "        authentication\n" +
            " from oauth_access_token where ";

    private static final String QUERY_REFRESH_TOKEN_BY_ID = "select refresh_id,\n" +
            "        token_id,\n" +
            "        client_id,\n" +
            "        expires_at\n" +
            " from oauth_refresh_token where ";

    private static final String INSERT_ACCESS_TOKEN = "insert into oauth_access_token \n" +
            "        (token_id,refresh_id,user_name,client_id,expires_at,creation,authentication)" +
            " values (?,?,?,?,?,?,?);";

    private static final String INSERT_REFRESH_TOKEN = "insert into oauth_refresh_token \n" +
            "        (refresh_id,token_id,user_name,client_id,expires_at)" +
            " values (?,?,?,?,?)";

    private static final String DELETE_ACCESS_TOKEN = "delete from oauth_access_token  where token_id = ?";

    private static final String DELETE_REFRESH_TOKEN = "delete from oauth_refresh_token where refresh_id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Oauth2AccessToken findAccessToken(String tokenId) {
        return jdbcTemplate.query(QUERY_ACCESS_TOKEN_BY_ID + "token_id = ?", this::rs2AcccessToken, tokenId).stream().findFirst().orElse(null);
    }

    private Oauth2AccessToken rs2AcccessToken(ResultSet rs, int i) throws SQLException {
        return Oauth2AccessToken.builder()
                .tokenId(rs.getString("token_id"))
                .refreshId(rs.getString("refresh_id"))
                .clientId(rs.getString("client_id"))
                .expires(rs.getTimestamp("expires_at"))
                .creation(rs.getTimestamp("creation"))
                .username(rs.getString("user_name"))
                .authentication(Base64.encodeBase64String(rs.getBytes("authentication")))
                .build();
    }

    @Override
    public Oauth2RefreshToken findRefreshToken(String refreshToken) {
        return jdbcTemplate.query(QUERY_REFRESH_TOKEN_BY_ID + "refresh_id = ?", this::rs2RefreshToken, refreshToken).stream().findFirst().orElse(null);
    }

    private Oauth2RefreshToken rs2RefreshToken(ResultSet rs, int i) throws SQLException {
        return Oauth2RefreshToken.builder()
                .refreshId(rs.getString(1))
                .tokenId(rs.getString(2))
                .clientId(rs.getString(3))
                .expires(rs.getTimestamp(4))
                .build();
    }

    @Override
    public void storeAccessToken(Oauth2AccessToken tokenAuth) {
        jdbcTemplate.update(INSERT_ACCESS_TOKEN, tokenAuth.getTokenId(),
                tokenAuth.getRefreshId(), tokenAuth.getUsername(),
                tokenAuth.getClientId(), tokenAuth.getExpires(),
                tokenAuth.getCreation(), Base64.decodeBase64(tokenAuth.getAuthentication()));
    }

    @Override
    public void storeRefreshToken(Oauth2RefreshToken refreshToken) {
        jdbcTemplate.update(INSERT_REFRESH_TOKEN, refreshToken.getRefreshId(),
                refreshToken.getTokenId(), refreshToken.getUsername(),
                refreshToken.getClientId(), refreshToken.getExpires());
    }

    @Override
    public void removeRefreshToken(Oauth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getRefreshId());
    }

    @Override
    public void removeAccessToken(Oauth2AccessToken token) {
        removeAccessToken(token.getTokenId());
    }

    @Override
    public void removeRefreshToken(String refreshId) {
        jdbcTemplate.update(DELETE_REFRESH_TOKEN, refreshId);
    }

    @Override
    public void removeAccessToken(String tokenId) {
        jdbcTemplate.update(DELETE_ACCESS_TOKEN, tokenId);
    }

    @Override
    public List<Oauth2AccessToken> findAccessTokensByUsername(String username) {
        RowMapper<Oauth2AccessToken> rowMapper = this::rs2AcccessToken;
        return jdbcTemplate.query(QUERY_ACCESS_TOKEN_BY_ID + "user_name = ?", rowMapper, username);
    }

    @Override
    public List<Oauth2RefreshToken> findRefreshTokensByUsername(String username) {
        RowMapper<Oauth2RefreshToken> rowMapper = this::rs2RefreshToken;
        return jdbcTemplate.query(QUERY_REFRESH_TOKEN_BY_ID + "user_name = ?", rowMapper, username);
    }
}
