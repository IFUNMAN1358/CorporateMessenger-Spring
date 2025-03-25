package com.nagornov.CorporateMessenger.application.advice;

import com.nagornov.CorporateMessenger.domain.model.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Order(1)
@Slf4j
public class HttpExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "HTTP method not supported: " + ex.getMethod(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
