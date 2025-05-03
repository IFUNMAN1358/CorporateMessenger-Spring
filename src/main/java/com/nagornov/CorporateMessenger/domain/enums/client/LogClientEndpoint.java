package com.nagornov.CorporateMessenger.domain.enums.client;

import lombok.Getter;

@Getter
public enum LogClientEndpoint {

    // POST :: SEND LOG
    SEND_LOG("/api/v1/log/send");

    private final String endpoint;

    LogClientEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

}
