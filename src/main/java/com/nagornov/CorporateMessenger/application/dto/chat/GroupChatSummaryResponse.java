package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupChatSummaryResponse {

    private GroupChat groupChat;
    private Message lastMessage;
    private User lastMessageSender;
    private UnreadMessage unreadMessage;

}
