package com.nagornov.CorporateMessenger.domain.exception;

import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.Getter;

import java.util.List;

@Getter
public class ResourceBadRequestException extends HttpException {
    public ResourceBadRequestException(String message) {
        super(message);
    }

    public ResourceBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceBadRequestException(String message, FieldError fieldError) {
        super(message, fieldError);
    }

    public ResourceBadRequestException(String message, List<FieldError> fieldErrors) {
        super(message, fieldErrors);
    }

    public ResourceBadRequestException(String message, Throwable cause, FieldError fieldError) {
        super(message, cause, fieldError);
    }

    public ResourceBadRequestException(String message, Throwable cause, List<FieldError> fieldErrors) {
        super(message, cause, fieldErrors);
    }
}
