package com.nagornov.CorporateMessenger.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChatMember {

    private Long chatId;
    private UUID userId;
    private String status;
    private Instant joinedAt;
    private Instant updatedAt;

}