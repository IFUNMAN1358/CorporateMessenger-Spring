package com.nagornov.CorporateMessenger.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UnreadMessage {

    private UUID chatId;
    private UUID userId;
    private Integer unreadCount;

    public void incrementUnreadCount() {
        this.unreadCount++;
    }

    public void decrementUnreadCount() {
        if (unreadCount > 0) {
            this.unreadCount--;
        }
    }

}