package com.nagornov.CorporateMessenger.domain.model;

import com.nagornov.CorporateMessenger.domain.enums.ChatType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private Boolean isAvailable = true;

    public void updateFirstUserId(@NotNull UUID newFirstUserId) {
        this.firstUserId = newFirstUserId;
    }

    public void updateSecondUserId(@NotNull UUID newSecondUserId) {
        this.secondUserId = newSecondUserId;
    }

    @Override
    public void updateLastMessageId(@NotNull UUID lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void markAsUnavailable() {
        this.isAvailable = false;
    }

    public void validateUserIdOwnership(@NotNull UUID userId) {
        if (!userId.equals(this.firstUserId) && !userId.equals(this.secondUserId)) {
            throw new RuntimeException("User is not in a private chat");
        }
    }

    @Override
    public void validateMessageChatIdOwnership(@NotNull UUID messageChatId) {
        if (!messageChatId.equals(this.getId())) {
            throw new RuntimeException("Message does not belong to private chat");
        }
    }

    @Override
    public String getChatType() {
        return ChatType.PRIVATE_CHAT.getType();
    }

    public UUID getCompanionUserId(@NotNull UUID userId) {
        if (this.firstUserId.equals(userId)) {
            return this.secondUserId;
        } else if (this.secondUserId.equals(userId)) {
            return this.firstUserId;
        } else {
            throw new RuntimeException("User is not in a private chat");
        }
    }

}