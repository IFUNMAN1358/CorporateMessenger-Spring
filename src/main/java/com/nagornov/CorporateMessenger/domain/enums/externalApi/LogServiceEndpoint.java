package com.nagornov.CorporateMessenger.domain.enums.externalApi;

import lombok.Getter;

@Getter
public enum LogServiceEndpoint {

    // POST
    SEND_LOG("/api/v1/log/send");

    private final String endpoint;

    LogServiceEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

}
