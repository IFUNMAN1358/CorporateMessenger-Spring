package com.nagornov.CorporateMessenger.application.advice;

import com.nagornov.CorporateMessenger.domain.exception.ApiError;
import com.nagornov.CorporateMessenger.domain.logger.AdviceLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AdviceLogger adviceLogger;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleUnhandledException(Throwable ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "Unhandled error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        adviceLogger.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
