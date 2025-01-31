package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.model.Message;
import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.domain.model.UnreadMessage;
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