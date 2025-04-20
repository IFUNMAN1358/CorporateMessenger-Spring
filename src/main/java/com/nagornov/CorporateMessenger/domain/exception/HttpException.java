package com.nagornov.CorporateMessenger.domain.exception;

import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HttpException extends RuntimeException {

    private List<FieldError> fieldErrors = new ArrayList<>();

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(String message, FieldError fieldError) {
        super(message);
        this.fieldErrors.add(fieldError);
    }

    public HttpException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public HttpException(String message, Throwable cause, FieldError fieldError) {
        super(message, cause);
        this.fieldErrors.add(fieldError);
    }

    public HttpException(String message, Throwable cause, List<FieldError> fieldErrors) {
        super(message, cause);
        this.fieldErrors = fieldErrors;
    }

}
