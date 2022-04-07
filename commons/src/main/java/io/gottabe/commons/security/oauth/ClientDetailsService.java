package io.gottabe.commons.security.oauth;

public interface ClientDetailsService {

    /**
     * Load a client by the client id. This method must not return null.
     *
     * @param clientId The client id.
     * @return The client details (never null).
     * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other reason.
     */
    ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;

}