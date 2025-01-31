package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UnreadMessage {

    private UUID chatId;
    private UUID userId;
    private Integer unreadCount;

    public void updateChatId(@NotNull UUID newChatId) {
        this.chatId = newChatId;
    }

    public void updateUserId(@NotNull UUID newUserId) {
        this.userId = newUserId;
    }

    public void incrementUnreadCount() {
        this.unreadCount++;
    }

    public void decrementUnreadCount() {
        if (unreadCount > 0) {
            this.unreadCount--;
        }
    }

}