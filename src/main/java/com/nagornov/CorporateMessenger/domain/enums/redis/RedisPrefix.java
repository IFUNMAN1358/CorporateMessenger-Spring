package com.nagornov.CorporateMessenger.domain.enums.redis;

import lombok.Getter;

@Getter
public enum RedisPrefix {

    SESSION("session"),
    MESSAGE("message"),
    CSRF("csrf");

    private final String prefix;

    RedisPrefix(String prefix) {
        this.prefix = prefix;
    }

}
