package com.nagornov.CorporateMessenger.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GroupChatMember {

    private UUID id;
    private UUID chatId;
    private UUID userId;
    private String userFirstName;
    private Instant joinedAt;

    public void updateChatId(UUID newChatId) {
        this.chatId = newChatId;
    }

    public void updateUserId(UUID newUserId) {
        this.userId = newUserId;
    }

    public void updateUserFirstName(String newUserFirstName) {
        this.userFirstName = newUserFirstName;
    }

}