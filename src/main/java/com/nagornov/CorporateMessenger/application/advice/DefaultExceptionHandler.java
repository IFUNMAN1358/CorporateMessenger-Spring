package com.nagornov.CorporateMessenger.application.advice;

import com.nagornov.CorporateMessenger.domain.model.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Order(2)
@Slf4j
public class DefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage().isEmpty() || ex.getMessage() == null ? "An unexpected error occurred" : ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleException(RuntimeException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "A runtime error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleException(IllegalArgumentException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

}
