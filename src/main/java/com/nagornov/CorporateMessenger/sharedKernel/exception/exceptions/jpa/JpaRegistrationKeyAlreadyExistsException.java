package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class JpaRegistrationKeyAlreadyExistsException extends ResponseStatusException {

    private static final int STATUS = 409;
    private static final String REASON = "Registration key is already exists in postgres database";

    public JpaRegistrationKeyAlreadyExistsException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
