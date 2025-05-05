package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ChatMemberDTO {

    private Long chatId;
    private User user;
    private String status;
    private Instant joinedAt;
    private Instant updatedAt;

}
