package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.GroupChatMember;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupChatWithMembersResponse {

    private GroupChat groupChat;
    private List<GroupChatMember> groupChatMembers;

}
