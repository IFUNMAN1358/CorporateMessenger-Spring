package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Message {

    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String senderFirstName;
    private String senderUsername;
    private String content;
    private Boolean hasFiles;
    private Boolean isChanged;
    private Boolean isRead;
    private Instant createdAt;

    public void updateId() {
        this.id = UUID.randomUUID();
    }

    public void updateChatId(@NotNull UUID newChatId) {
        this.chatId = newChatId;
    }

    public void updateSenderId(@NotNull UUID newSenderId) {
        this.senderId = newSenderId;
    }

    public void updateSenderFirstName(@NotNull String newSenderFirstName) {
        this.senderFirstName = newSenderFirstName;
    }

    public void updateSenderUsername(@NotNull String newSenderUsername) {
        this.senderUsername = newSenderUsername;
    }

    public void updateContent(@NotNull String newContent) {
        this.content = newContent;
    }

    public void markAsHasFiles() {
        this.hasFiles = true;
    };

    public void unmarkAsHasFiles() {
        this.hasFiles = false;
    }

    public void markAsChanged() {
        this.isChanged = true;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void validateUserIdOwnership(@NotNull UUID userId) {
        if (!userId.equals(this.getSenderId())) {
            throw new RuntimeException("Message does not belong to the user");
        }
    }

    public void validateContentBeforeUpdate(String newContent) {
        boolean isContentEmpty = this.getContent() == null || this.getContent().trim().isEmpty();
        boolean isNewContentEmpty = newContent == null || newContent.trim().isEmpty();

        if (!this.getHasFiles() && isContentEmpty && isNewContentEmpty) {
            throw new RuntimeException("Message cannot be without files and without content");
        };
    }

}
