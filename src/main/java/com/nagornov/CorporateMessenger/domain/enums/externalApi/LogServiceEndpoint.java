package com.nagornov.CorporateMessenger.domain.enums.externalApi;

import lombok.Getter;

@Getter
public enum LogServiceEndpoint {

    // POST
    SEND_LOG("/api/v1/log/send"),

    // GET
    GET_CSRF_TOKEN("/api/v1/csrf");

    private final String endpoint;

    LogServiceEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

}
