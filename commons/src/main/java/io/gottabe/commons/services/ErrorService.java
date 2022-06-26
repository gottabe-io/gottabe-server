package io.gottabe.commons.services;

import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.util.Messages;
import io.gottabe.commons.vo.ErrorFieldVO;
import io.gottabe.commons.vo.ErrorVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private Messages messages;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorVO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        if (debug) log.error("Error captured: ", ex);
        ErrorVO errors = new ErrorVO();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = messages.getString(error.getDefaultMessage());
            errors.getFields().add(new ErrorFieldVO(fieldName, errorMessage));
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseBody
    public ErrorVO handleValidationExceptions(
            InvalidRequestException ex) {
        return getErrorVO(ex);
    }

    private ErrorVO getErrorVO(Exception ex) {
        if (debug) log.error("Error captured: ", ex);
        ErrorVO errors = new ErrorVO();
        errors.setMessage(messages.getString(ex.getMessage()));
        return errors;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ErrorVO handleAccessDeniedException(
            AccessDeniedException ex) {
        return getErrorVO(ex);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ErrorVO handleAuthenticationException(
            AuthenticationException ex) {
        return getErrorVO(ex);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ErrorVO handleNotFoundExceptions(
            Exception ex) {
        return getErrorVO(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorVO handleOtherExceptions(
            Exception ex) {
        return getErrorVO(ex);
    }

}
