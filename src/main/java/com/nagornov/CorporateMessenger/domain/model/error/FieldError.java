package com.nagornov.CorporateMessenger.domain.model.error;

import lombok.Getter;
import lombok.NonNull;

@Getter
final public class FieldError {

    private final String fieldKey;
    private final String fieldValue;

    public FieldError(@NonNull String fieldKey, @NonNull String fieldValue) {
        this.fieldKey = fieldKey;
        this.fieldValue = fieldValue;
    }
}
