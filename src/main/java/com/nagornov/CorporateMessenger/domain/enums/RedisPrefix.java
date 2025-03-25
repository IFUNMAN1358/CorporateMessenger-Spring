package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum RedisPrefix {

    SESSION("session"),
    MESSAGE("message");

    private final String prefix;

    RedisPrefix(String prefix) {
        this.prefix = prefix;
    }

}
