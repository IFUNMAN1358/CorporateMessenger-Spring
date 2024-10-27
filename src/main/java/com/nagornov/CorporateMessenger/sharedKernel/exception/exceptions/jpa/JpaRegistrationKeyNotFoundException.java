package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class JpaRegistrationKeyNotFoundException extends ResponseStatusException {

    private static final int STATUS = 404;
    private static final String REASON = "Registration key not found in postgres database";

    public JpaRegistrationKeyNotFoundException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
