package com.nagornov.CorporateMessenger.domain.factory;

import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class PrivateChatFactory {

    public static PrivateChat createWithRandomId() {
        return new PrivateChat(
                UUID.randomUUID(),
                null,
                null,
                null,
                Instant.now(),
                true
        );
    }

}
