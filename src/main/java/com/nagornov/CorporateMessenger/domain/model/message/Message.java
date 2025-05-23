package com.nagornov.CorporateMessenger.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Message {

    private UUID id;
    private Long chatId;
    private UUID senderId;
    private String senderUsername;
    private String content;
    private Boolean hasFiles;
    private Boolean isChanged;
    private Boolean isRead;
    private Instant createdAt;

    public void updateContent(@NonNull String newContent) {
        this.content = newContent;
    }

    public void markAsHasFiles() {
        this.hasFiles = true;
    }

    public void unmarkAsHasFiles() {
        this.hasFiles = false;
    }

    public void markAsChanged() {
        this.isChanged = true;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void ensureUserIdIsSenderId(@NonNull UUID userId) {
        if (!userId.equals(this.getSenderId())) {
            throw new RuntimeException("Message does not belong to the user");
        }
    }

}
