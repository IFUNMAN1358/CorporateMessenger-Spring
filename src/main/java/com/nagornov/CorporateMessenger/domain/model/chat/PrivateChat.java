package com.nagornov.CorporateMessenger.domain.model.chat;

import com.nagornov.CorporateMessenger.domain.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrivateChat implements Chat {

    private UUID id;
    private UUID firstUserId;
    private UUID secondUserId;
    private UUID lastMessageId;
    private Instant createdAt;
    private Boolean isAvailable;

    @Override
    public void updateLastMessageId(UUID lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void markAsUnavailable() {
        this.isAvailable = false;
    }

    public void validateUserIdOwnership(@NonNull UUID userId) {
        if (!userId.equals(this.firstUserId) && !userId.equals(this.secondUserId)) {
            throw new RuntimeException("User is not in a private chat");
        }
    }

    @Override
    public void validateMessageChatIdOwnership(@NonNull UUID messageChatId) {
        if (!messageChatId.equals(this.getId())) {
            throw new RuntimeException("Message does not belong to private chat");
        }
    }

    @Override
    public String getChatType() {
        return ChatType.PRIVATE_CHAT.getType();
    }

    public UUID getCompanionUserId(@NonNull UUID userId) {
        if (this.firstUserId.equals(userId)) {
            return this.secondUserId;
        } else if (this.secondUserId.equals(userId)) {
            return this.firstUserId;
        } else {
            throw new RuntimeException("User is not in a private chat");
        }
    }

}