package io.gottabe.commons.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AnyAuthenticationException extends AuthenticationException {
    public AnyAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AnyAuthenticationException(String msg) {
        super(msg);
    }
}
