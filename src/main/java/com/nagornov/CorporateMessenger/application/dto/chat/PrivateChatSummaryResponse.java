package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrivateChatSummaryResponse {

    private UserResponseWithMainPhoto secondUser;
    private PrivateChat privateChat;
    private Message lastMessage;
    private UnreadMessage unreadMessage;

}