package com.nagornov.CorporateMessenger.domain.exception;

import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.Getter;

import java.util.List;

@Getter
public class ResourceForbiddenException extends HttpException {

    public ResourceForbiddenException(String message) {
        super(message);
    }

    public ResourceForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceForbiddenException(String message, FieldError fieldError) {
        super(message, fieldError);
    }

    public ResourceForbiddenException(String message, List<FieldError> fieldErrors) {
        super(message, fieldErrors);
    }

    public ResourceForbiddenException(String message, Throwable cause, FieldError fieldError) {
        super(message, cause, fieldError);
    }

    public ResourceForbiddenException(String message, Throwable cause, List<FieldError> fieldErrors) {
        super(message, cause, fieldErrors);
    }
}
