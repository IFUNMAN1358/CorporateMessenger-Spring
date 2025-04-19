package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatSummaryResponse {

    private UserResponseWithMainPhoto secondUser;
    private Chat chat;
    private Message lastMessage;
    private UnreadMessage unreadMessage;

}