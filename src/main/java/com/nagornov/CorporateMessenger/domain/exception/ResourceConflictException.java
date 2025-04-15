package com.nagornov.CorporateMessenger.domain.exception;

import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class ResourceConflictException extends RuntimeException {

    private final List<FieldError> fieldErrors;

    public ResourceConflictException(String message) {

        super(message);
        fieldErrors = Collections.emptyList();

    }

    public ResourceConflictException(String message, List<FieldError> fieldErrors) {

        super(message);
        this.fieldErrors = fieldErrors;

    }
}
