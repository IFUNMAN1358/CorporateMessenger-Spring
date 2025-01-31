package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadMessage {

    private UUID id;
    private UUID userId;
    private UUID chatId;
    private UUID messageId;

    public void updateUserId(@NotNull UUID userId) {
        this.userId = userId;
    }

    public void updateChatId(@NotNull UUID chatId) {
        this.chatId = chatId;
    }

    public void updateMessageId(@NotNull UUID messageId) {
        this.messageId = messageId;
    }

}
