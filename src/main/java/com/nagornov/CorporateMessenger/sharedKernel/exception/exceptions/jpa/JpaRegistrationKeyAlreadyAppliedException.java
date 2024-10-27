package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class JpaRegistrationKeyAlreadyAppliedException extends ResponseStatusException {

    private static final int STATUS = 409;
    private static final String REASON = "Registration key is already applied in postgres database";

    public JpaRegistrationKeyAlreadyAppliedException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
