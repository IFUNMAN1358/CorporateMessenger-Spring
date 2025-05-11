package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.utils;

import com.nagornov.CorporateMessenger.domain.enums.redis.RedisPrefix;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class RedisKeyUtils {

    private final static String SESSION = RedisPrefix.SESSION.getPrefix();
    private final static String MESSAGE = RedisPrefix.MESSAGE.getPrefix();

    public static String sessionKey(UUID userId) {
        return "%s:%s".formatted(SESSION, userId);
    }

    public static String messageKey(Long chatId) {
        return "%s:%s".formatted(MESSAGE, chatId);
    }

}
