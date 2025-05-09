package com.nagornov.CorporateMessenger.domain.model.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatMemberStatus;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChatMember {

    private Long chatId;
    private UUID userId;
    private ChatMemberStatus status;
    private Instant joinedAt;
    private Instant updatedAt;

    public boolean isCreator() {
        return status.equals(ChatMemberStatus.CREATOR);
    }

    public boolean isAdministrator() {
        return status.equals(ChatMemberStatus.ADMINISTRATOR);
    }

    public boolean isMember() {
        return status.equals(ChatMemberStatus.MEMBER);
    }

    public boolean isRestricted() {
        return status.equals(ChatMemberStatus.RESTRICTED);
    }

    public boolean isLeft() {
        return status.equals(ChatMemberStatus.LEFT);
    }

    public boolean isKicked() {
        return status.equals(ChatMemberStatus.KICKED);
    }

    public void ensureIsCreator() {
        if (!isCreator()) {
            throw new ResourceBadRequestException("You dont have permission because you are not the creator of the group chat");
        }
    }

}