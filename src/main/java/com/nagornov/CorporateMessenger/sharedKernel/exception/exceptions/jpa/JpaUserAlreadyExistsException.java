package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class JpaUserAlreadyExistsException extends ResponseStatusException {

    private static final int STATUS = 409;
    private static final String REASON = "User is already exists in postgres database";

    public JpaUserAlreadyExistsException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
