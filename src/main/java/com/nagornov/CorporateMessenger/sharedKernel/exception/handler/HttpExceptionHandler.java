package com.nagornov.CorporateMessenger.sharedKernel.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleHttpException(ResponseStatusException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("reason", ex.getReason());
        response.put("status", ex.getStatusCode());

        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }
}