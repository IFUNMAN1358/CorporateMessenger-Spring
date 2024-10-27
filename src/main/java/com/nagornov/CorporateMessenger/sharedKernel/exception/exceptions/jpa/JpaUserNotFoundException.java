package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class JpaUserNotFoundException extends ResponseStatusException {

    private static final int STATUS = 404;
    private static final String REASON = "User not found in postgres database";

    public JpaUserNotFoundException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
