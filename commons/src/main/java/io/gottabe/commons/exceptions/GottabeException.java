package io.gottabe.commons.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GottabeException extends RuntimeException {
    public GottabeException() {
    }

    public GottabeException(String message) {
        super(message);
    }

    public GottabeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GottabeException(Throwable cause) {
        super(cause);
    }

    public GottabeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
