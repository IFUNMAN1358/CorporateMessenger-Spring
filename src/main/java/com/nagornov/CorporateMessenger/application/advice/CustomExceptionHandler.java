package com.nagornov.CorporateMessenger.application.advice;

import com.nagornov.CorporateMessenger.domain.model.error.ApiError;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(1)
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiError> handleException(ResourceConflictException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                null,
                LocalDateTime.now()
        );
        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BindingErrorException.class)
    public ResponseEntity<ApiError> handleUnprocessableEntityException(BindingErrorException ex, HttpServletRequest request) {

        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> validationErrors = new HashMap<>();

        bindingResult.getFieldErrors().forEach(e -> {
            validationErrors.put(e.getField(), e.getDefaultMessage());
        });

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                validationErrors,
                LocalDateTime.now()
        );

        log.error(apiError.toString(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
