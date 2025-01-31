package com.nagornov.CorporateMessenger.domain.factory;

import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class GroupChatFactory {

    public static GroupChat createWithRandomId() {
        return new GroupChat(
                UUID.randomUUID(),
                null,
                null,
                null,
                null,
                null,
                false,
                Instant.now(),
                Instant.now()
        );
    }

}
