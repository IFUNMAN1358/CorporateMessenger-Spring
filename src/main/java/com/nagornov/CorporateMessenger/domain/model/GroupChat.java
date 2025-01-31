package com.nagornov.CorporateMessenger.domain.model;

import com.nagornov.CorporateMessenger.domain.enums.ChatType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GroupChat implements Chat {

    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private String filePath;
    private UUID lastMessageId;
    private Boolean isPublic;
    private Instant updatedAt;
    private Instant createdAt;

    public void updateName(@NotNull String newName) {
        this.name = newName;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(@NotNull String newDescription) {
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }

    public void updateOwnerId(@NotNull UUID newOwnerId) {
        this.ownerId = newOwnerId;
        this.updatedAt = Instant.now();
    }

    public void updateFilePath(@NotNull String newFilePath) {
        this.filePath = newFilePath;
        this.updatedAt = Instant.now();
    }

    @Override
    public void updateLastMessageId(@NotNull UUID newLastMessageId) {
        this.lastMessageId = newLastMessageId;
        this.updatedAt = Instant.now();
    }

    @Override
    public void validateMessageChatIdOwnership(@NotNull UUID messageChatId) {
        if (!messageChatId.equals(this.getId())) {
            throw new RuntimeException("Message does not belong to private chat");
        }
    }

    @Override
    public String getChatType() {
        return ChatType.GROUP_CHAT.getType();
    }

    public void markAsPrivate() {
        this.isPublic = false;
        this.updatedAt = Instant.now();
    }

    public void markAsPublic() {
        this.isPublic = true;
        this.updatedAt = Instant.now();
    }

    public void validateMemberChatIdOwnership(@NotNull UUID memberChatId) {
        if (!memberChatId.equals(this.id)) {
            throw new RuntimeException("User is not in a group chat");
        }
    }

    public void validateOwnerIdOwnership(@NotNull UUID userId) {
        if (!this.ownerId.equals(userId)) {
            throw new RuntimeException("This user id is not the owner's id");
        }
    }

}