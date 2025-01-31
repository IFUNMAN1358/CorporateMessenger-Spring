package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.common;

import com.nagornov.CorporateMessenger.domain.enums.RedisPrefix;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class RedisUtils {

    private final static String SESSION = RedisPrefix.SESSION.getPrefix();

    public static String userSessionKey(@NotNull UUID userId) {
        return "%s:%s".formatted(SESSION, userId);
    }

}
