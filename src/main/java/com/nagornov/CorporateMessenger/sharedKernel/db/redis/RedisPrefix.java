package com.nagornov.CorporateMessenger.sharedKernel.db.redis;

import lombok.Getter;

@Getter
public enum RedisPrefix {

    USERS("users:"),
    SESSIONS("sessions:");

    private final String prefix;

    RedisPrefix(String prefix) {
        this.prefix = prefix;
    }

}
