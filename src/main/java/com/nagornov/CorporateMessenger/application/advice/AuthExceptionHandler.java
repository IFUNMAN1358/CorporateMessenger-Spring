package com.nagornov.CorporateMessenger.application.advice;

import com.nagornov.CorporateMessenger.domain.model.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Order(1)
@Slf4j
public class AuthExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleException(BadCredentialsException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleException(AccessDeniedException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

}
