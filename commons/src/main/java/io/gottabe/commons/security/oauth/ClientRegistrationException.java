package io.gottabe.commons.security.oauth;

@SuppressWarnings("serial")
public class ClientRegistrationException extends RuntimeException {

    public ClientRegistrationException(String msg) {
        super(msg);
    }

    public ClientRegistrationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}