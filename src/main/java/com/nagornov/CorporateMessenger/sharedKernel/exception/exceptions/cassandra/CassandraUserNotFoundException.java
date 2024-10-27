package com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.cassandra;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CassandraUserNotFoundException extends ResponseStatusException {

    private static final int STATUS = 404;
    private static final String REASON = "User not found in cassandra database";

    public CassandraUserNotFoundException() {
        super(HttpStatusCode.valueOf(STATUS), REASON);
    }
}
