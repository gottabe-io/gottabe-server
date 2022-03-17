package io.gottabe.commons.services;

import io.gottabe.commons.vo.ErrorFieldVO;
import io.gottabe.commons.vo.ErrorVO;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
@ControllerAdvice
@Slf4j
public class ErrorService {

    @Value("${gottabeio.debug}")
    private boolean debug;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorVO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        if (debug) log.error("Error captured: ", ex);
        ErrorVO errors = new ErrorVO();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.getFields().add(new ErrorFieldVO(fieldName, errorMessage));
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ErrorVO handleAccessDeniedException(
            AccessDeniedException ex) {
        if (debug) log.error("Error captured: ", ex);
        ErrorVO errors = new ErrorVO();
        errors.setMessage(ex.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ErrorVO handleAuthenticationException(
            AuthenticationException ex) {
        if (debug) log.error("Error captured: ", ex);
        ErrorVO errors = new ErrorVO();
        errors.setMessage(ex.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorVO handleOtherExceptions(
            Exception ex) {
        if (debug) log.error("Error captured: ", ex);
        ErrorVO errors = new ErrorVO();
        errors.setMessage(ex.getMessage());
        return errors;
    }

}
