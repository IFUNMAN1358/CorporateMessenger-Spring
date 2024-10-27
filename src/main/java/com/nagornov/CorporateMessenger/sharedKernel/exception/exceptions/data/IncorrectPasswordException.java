package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.data;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class IncorrectPasswordException extends ResponseStatusException {

    private static final int STATUS = 400;
    private static final String REASON = "Incorrect password";

    public IncorrectPasswordException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
