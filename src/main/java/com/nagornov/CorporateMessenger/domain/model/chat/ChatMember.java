package com.nagornov.CorporateMessenger.domain.model.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChatMember {

    private Long chatId;
    private UUID userId;
    private String status;
    private Instant joinedAt;
    private Instant updatedAt;

    public boolean isCreator() {
        return status.equals(ChatMemberStatus.CREATOR.getStatus());
    }

    public boolean isAdministrator() {
        return status.equals(ChatMemberStatus.ADMINISTRATOR.getStatus());
    }

    public boolean isMember() {
        return status.equals(ChatMemberStatus.MEMBER.getStatus());
    }

    public boolean isRestricted() {
        return status.equals(ChatMemberStatus.RESTRICTED.getStatus());
    }

    public boolean isLeft() {
        return status.equals(ChatMemberStatus.LEFT.getStatus());
    }

    public boolean isKicked() {
        return status.equals(ChatMemberStatus.KICKED.getStatus());
    }

}