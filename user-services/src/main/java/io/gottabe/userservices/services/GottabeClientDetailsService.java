package io.gottabe.userservices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.builders.JdbcClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class GottabeClientDetailsService implements ClientDetailsService {

    private ClientDetailsService delegate;

    public GottabeClientDetailsService(DataSource dataSource) throws Exception {
        delegate = new JdbcClientDetailsServiceBuilder()
                .dataSource(dataSource)
                .build();
    }


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return delegate.loadClientByClientId(clientId);
    }
}
