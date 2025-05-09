package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatType;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;

@Data
public class ChatResponse {

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

    public ChatResponse(@NonNull Chat chat) {
        this.id = chat.getId();
        this.type = chat.getType();
        this.username = chat.getUsername();
        this.title = chat.getTitle();
        this.description = chat.getDescription();
        this.inviteLink = chat.getInviteLink();
        this.joinByRequest = chat.getJoinByRequest();
        this.hasHiddenMembers = chat.getHasHiddenMembers();
        this.createdAt = chat.getCreatedAt();
        this.updatedAt = chat.getUpdatedAt();
    }

}
