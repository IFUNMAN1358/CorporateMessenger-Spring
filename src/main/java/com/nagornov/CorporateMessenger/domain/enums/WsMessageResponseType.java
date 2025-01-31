package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum WsMessageResponseType {

    CREATE("CREATE"),
    UPDATE_CONTENT("UPDATE_CONTENT"),
    DELETE("DELETE"),
    READ("READ"),
    GET_ALL("GET_ALL"),
    GET_FILE("GET_FILE");

    private final String type;

    WsMessageResponseType(String type) {
        this.type = type;
    }
}
