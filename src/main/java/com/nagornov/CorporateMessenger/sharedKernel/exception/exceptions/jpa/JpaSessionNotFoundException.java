package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class JpaSessionNotFoundException extends ResponseStatusException {

    private static final int STATUS = 404;
    private static final String REASON = "Session not found in postgres database";

    public JpaSessionNotFoundException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
