package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.cassandra;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CassandraUserAlreadyExistsException extends ResponseStatusException {

    private static final int STATUS = 409;
    private static final String REASON = "User is already exists in cassandra database";

    public CassandraUserAlreadyExistsException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
