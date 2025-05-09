package com.nagornov.CorporateMessenger.domain.model.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatType;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Chat {

    private Long id;
    private ChatType type;
    private String username;
    private String title;
    private String description;
    private String inviteLink;
    private Boolean joinByRequest;
    private Boolean hasHiddenMembers;
    private Instant createdAt;
    private Instant updatedAt;

//    private Long id;                     ALL | primaryKey=true, unique=true, nullable=false, updatable=false
//    private String type;                 ALL | length=16, unique=false, nullable=false, updatable=false
//    private String username;             GROUP | length=5-32, unique=true, nullable=true, updatable=true
//    private String title;                GROUP | length=1-128, unique=false, nullable=true, updatable=true
//    private String description;          GROUP | length=255, unique=false, nullable=true, updatable=true
//    private String inviteLink;           GROUP | length=32, unique=true, nullable=true, updatable=true
//    private Boolean joinByRequest;       GROUP | nullable=true, updatable=true
//    private Boolean hasHiddenMembers;    GROUP | nullable=true, updatable=true
//    private Instant createdAt;           ALL | nullable=false, updatable=false
//    private Instant updatedAt;           ALL | nullable=false, updatable=true

    public static Long generateId() {
        return UUID.randomUUID().getMostSignificantBits() + Long.MAX_VALUE;
    }

    public void updateGeneratedId() {
        this.id = UUID.randomUUID().getMostSignificantBits() + Long.MAX_VALUE;
    }

    public void updateId(Long newId) {
        if (id == null) {
            throw new ResourceConflictException("Chat[id] already set");
        }
    }

    public boolean isPrivateChat() {
        return this.type.equals(ChatType.PRIVATE);
    }

    public boolean isGroupChat() {
        return this.type.equals(ChatType.GROUP);
    }

    public void ensureIsGroupChat() {
        if (!this.isGroupChat()) {
            throw new ResourceBadRequestException("This is not a group chat");
        }
    }

    public void ensureIsPrivateChat() {
        if (!this.isPrivateChat()) {
            throw new ResourceBadRequestException("This is not a private chat");
        }
    }
}