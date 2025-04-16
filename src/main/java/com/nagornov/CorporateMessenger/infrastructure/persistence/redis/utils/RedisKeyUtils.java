package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.utils;

import com.nagornov.CorporateMessenger.domain.enums.RedisPrefix;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class RedisKeyUtils {

    private final static String JWT_SESSION = RedisPrefix.JWT_SESSION.getPrefix();
    private final static String MESSAGE = RedisPrefix.MESSAGE.getPrefix();

    public static String jwtSessionKey(UUID userId) {
        return "%s:%s".formatted(JWT_SESSION, userId);
    }

    public static String messageKey(UUID chatId) {
        return "%s:%s".formatted(MESSAGE, chatId);
    }

}
