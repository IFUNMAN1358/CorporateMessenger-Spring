package com.nagornov.CorporateMessenger.domain.exception;

import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.Getter;

import java.util.List;

@Getter
public class ResourceConflictException extends HttpException {

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceConflictException(String message, FieldError fieldError) {
        super(message, fieldError);
    }

    public ResourceConflictException(String message, List<FieldError> fieldErrors) {
        super(message, fieldErrors);
    }

    public ResourceConflictException(String message, Throwable cause, FieldError fieldError) {
        super(message, cause, fieldError);
    }

    public ResourceConflictException(String message, Throwable cause, List<FieldError> fieldErrors) {
        super(message, cause, fieldErrors);
    }
}
