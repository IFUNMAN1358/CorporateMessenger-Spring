package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface Chat {

    UUID getId();
    void updateLastMessageId(@NotNull UUID newLastMessageId);
    void validateMessageChatIdOwnership(@NotNull UUID messageChatId);
    String getChatType();

}
