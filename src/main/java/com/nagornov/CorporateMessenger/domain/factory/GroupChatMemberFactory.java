package com.nagornov.CorporateMessenger.domain.factory;

import com.nagornov.CorporateMessenger.domain.model.GroupChatMember;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class GroupChatMemberFactory {

    public static GroupChatMember createWithRandomId() {
        return new GroupChatMember(
                UUID.randomUUID(),
                null,
                null,
                null,
                Instant.now()
        );
    }
}
