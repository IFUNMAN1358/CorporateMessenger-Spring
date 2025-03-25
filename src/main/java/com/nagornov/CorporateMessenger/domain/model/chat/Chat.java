package com.nagornov.CorporateMessenger.domain.model.chat;

import lombok.NonNull;

import java.util.UUID;

public interface Chat {

    UUID getId();

    void updateLastMessageId(UUID newLastMessageId);

    void validateMessageChatIdOwnership(UUID messageChatId);

    String getChatType();

}
