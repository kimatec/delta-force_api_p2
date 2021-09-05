package com.revature.deltaforce.web.util;

import com.revature.deltaforce.util.exceptions.*;
import com.revature.deltaforce.web.dtos.ErrorResponse;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ErrorResponseAspect {

    @ExceptionHandler({
            InvalidRequestException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestException(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            String defaultMessage = Objects.requireNonNull(((MethodArgumentNotValidException) e).getBindingResult().getFieldError()).getDefaultMessage();
            return new ErrorResponse(400, defaultMessage);
        }
        return new ErrorResponse(400, e.getMessage());
    }

    @ExceptionHandler({
            AuthenticationException.class,
            SignatureException.class,
            MalformedJwtException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(Exception e) {
        return new ErrorResponse(401, e.getMessage());
    }

    @ExceptionHandler({
            ResourceNotFoundException.class,
            ExternalDataSourceException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(Exception e) {
        return new ErrorResponse(404, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleResourcePersistenceException(ResourcePersistenceException e) {
        return new ErrorResponse(409, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Exception e) {
        return new ErrorResponse(500, e.getMessage());
    }
}
