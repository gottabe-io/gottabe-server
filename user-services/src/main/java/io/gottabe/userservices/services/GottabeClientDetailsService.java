package io.gottabe.userservices.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gottabe.commons.exceptions.GottabeException;
import io.gottabe.commons.security.oauth.ClientDetails;
import io.gottabe.commons.security.oauth.ClientDetailsService;
import io.gottabe.commons.security.oauth.ClientRegistrationException;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GottabeClientDetailsService implements ClientDetailsService {

    private static final String QUERY = "select client_id, \n" +
            "        resource_ids, \n" +
            "        client_secret, \n" +
            "        scope, \n" +
            "        authorized_grant_types, \n" +
            "        web_server_redirect_uri, \n" +
            "        authorities, \n" +
            "        access_token_validity, \n" +
            "        refresh_token_validity, \n" +
            "        additional_information, \n" +
            "        autoapprove \n" +
            "from oauth_client_details where client_id = ?";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return jdbcTemplate.queryForObject(QUERY, this::rowToDetails, clientId);
    }

    private ClientDetailsImpl rowToDetails(ResultSet rs, int i) throws SQLException {
        return ClientDetailsImpl.builder()
                .clientId(rs.getString(1))
                .resourceIds(toSet(rs.getString(2)))
                .clientSecret(rs.getString(3))
                .scope(toSet(rs.getString(4)))
                .authorizedGrantTypes(toSet(rs.getString(5)))
                .registeredRedirectUri(toSet(rs.getString(6)))
                .authorities(toSet(rs.getString(7)).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .accessTokenValiditySeconds(rs.getInt(8))
                .refreshTokenValiditySeconds(rs.getInt(9))
                .additionalInformation(fromJson(rs.getString(10)))
                .autoApproveScopes(toSet(rs.getString(11)))
                .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fromJson(String data) {
        try {
            return data == null ? null : new ObjectMapper().readValue(data, Map.class);
        } catch (JsonProcessingException e) {
            throw new GottabeException(e);
        }
    }

    private Set<String> toSet(String str) {
        return str != null
                ? Arrays.asList(str.split("\\s*,\\s*")).stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet())
                : Collections.emptySet();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClientDetailsImpl implements ClientDetails {
        private String clientId;
        private Set<String> resourceIds;
        private String clientSecret;
        private Set<String> scope;
        private Set<String> authorizedGrantTypes;
        private Set<String> registeredRedirectUri;
        private Collection<GrantedAuthority> authorities;
        private Integer accessTokenValiditySeconds;
        private Integer refreshTokenValiditySeconds;
        private Map<String, Object> additionalInformation;
        private Set<String> autoApproveScopes;
        public boolean isSecretRequired() {
            return clientSecret != null && !clientSecret.isEmpty();
        }
        public boolean isScoped() {
            return scope != null && !scope.isEmpty();
        }
        public boolean isAutoApprove(String scope){
            return autoApproveScopes.isEmpty() || autoApproveScopes.contains(scope);
        }
    }
}
