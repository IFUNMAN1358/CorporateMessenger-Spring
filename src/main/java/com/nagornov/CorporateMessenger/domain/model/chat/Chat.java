package com.nagornov.CorporateMessenger.domain.model.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatType;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.domain.utils.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

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

    public static Long generateId() {
        return SnowflakeIdGenerator.nextId();
    }

    public void updateTitle(String title) {
        if (title == null) {
            throw new ResourceConflictException(
                    "Title cannot be null",
                    new FieldError("title", "Название чата не может быть null")
            );
        }
        if (title.length() < 1 || title.length() > 128) {
            throw new ResourceConflictException(
                    "Title length should be between 1 and 128",
                    new FieldError("title", "Название чата должно иметь длину от 1 до 128 символов")
            );
        }
        this.title = title;
    }

    public void updateUsername(String username) {
        if (username == null) {
            throw new ResourceConflictException(
                    "Username cannot be null",
                    new FieldError("username", "Уникальное название чата не может быть null")
            );
        }
        if (username.length() < 5 || username.length() > 32) {
            throw new ResourceConflictException(
                    "Username length should be between 1 and 128",
                    new FieldError("username", "Уникальное название чата должно иметь длину от 5 до 32 символов")
            );
        }
        this.username = username;
    }

    public void updateDescription(String description) {
        if (description == null) {
            throw new ResourceConflictException(
                    "Description cannot be null",
                    new FieldError("description", "Описание чата не может быть null")
            );
        }
        if (description.length() > 255) {
            throw new ResourceConflictException(
                    "Description should not be longer than 255",
                    new FieldError("description", "Описание чата не может быть длиннее 255 символов")
            );
        }
        this.description = description;
    }

    public void updateJoinByRequest(Boolean joinByRequest) {
        if (joinByRequest == null) {
            throw new ResourceConflictException(
                    "Setting 'joinByRequest' cannot be null",
                    new FieldError("joinByRequest", "Настройка 'Присоединение по запросу' не может быть null")
            );
        }
        this.joinByRequest = joinByRequest;
    }

    public void updateHasHiddenMembers(Boolean hasHiddenMembers) {
        if (hasHiddenMembers == null) {
            throw new ResourceConflictException(
                    "Setting 'hasHiddenMembers' cannot be null",
                    new FieldError("hasHiddenMembers", "Настройка 'Имеет скрытых пользователей' не может быть null")
            );
        }
        this.hasHiddenMembers = hasHiddenMembers;
    }
}