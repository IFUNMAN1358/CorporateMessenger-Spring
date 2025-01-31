package com.nagornov.CorporateMessenger.domain.exception.custom;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
