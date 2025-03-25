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
@Order(3)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleUnhandledException(Throwable ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "Unhandled error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
