package com.nagornov.CorporateMessenger.domain.exception;

import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.Getter;

import java.util.List;

@Getter
public class ResourceNotFoundException extends HttpException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String message, FieldError fieldError) {
        super(message, fieldError);
    }

    public ResourceNotFoundException(String message, List<FieldError> fieldErrors) {
        super(message, fieldErrors);
    }

    public ResourceNotFoundException(String message, Throwable cause, FieldError fieldError) {
        super(message, cause, fieldError);
    }

    public ResourceNotFoundException(String message, Throwable cause, List<FieldError> fieldErrors) {
        super(message, cause, fieldErrors);
    }
}
