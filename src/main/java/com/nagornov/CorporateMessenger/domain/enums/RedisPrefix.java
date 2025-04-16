package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum RedisPrefix {

    JWT_SESSION("jwt-session"),
    MESSAGE("message"),
    CSRF("csrf");

    private final String prefix;

    RedisPrefix(String prefix) {
        this.prefix = prefix;
    }

}
