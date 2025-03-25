package com.nagornov.CorporateMessenger.domain.model.chat;

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

}