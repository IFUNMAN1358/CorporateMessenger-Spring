package com.nagornov.CorporateMessenger.domain.enums.redis;

import lombok.Getter;

@Getter
public enum RedisPrefix {

    SESSION("session"),
    MESSAGE("message"),
    EXTERNAL_SERVICES("external_services");

    private final String prefix;

    RedisPrefix(String prefix) {
        this.prefix = prefix;
    }

}
