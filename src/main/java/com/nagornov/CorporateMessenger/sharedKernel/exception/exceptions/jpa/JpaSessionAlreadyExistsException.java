package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class JpaSessionAlreadyExistsException extends ResponseStatusException {

    private static final int STATUS = 409;
    private static final String REASON = "Session is already exists in postgres database";

    public JpaSessionAlreadyExistsException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
