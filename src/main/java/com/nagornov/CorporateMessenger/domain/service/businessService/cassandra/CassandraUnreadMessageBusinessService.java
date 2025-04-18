package com.nagornov.CorporateMessenger.domain.service.businessService.cassandra;

import com.nagornov.CorporateMessenger.domain.model.chat.*;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatInterface;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraUnreadMessageDomainService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CassandraUnreadMessageBusinessService {

    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;

    public void incrementUnreadMessageCountForOther(@NonNull ChatInterface chatInterface, @NonNull User user) {
        if (chatInterface instanceof PrivateChat privateChat) {

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    privateChat.getId(), privateChat.getCompanionUserId(user.getId())
            );
            unreadMessage.incrementUnreadCount();
            cassandraUnreadMessageDomainService.save(unreadMessage);

        } else if (chatInterface instanceof GroupChat groupChat) {

            List<ChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
            for (ChatMember member : members) {
                if (!member.getUserId().equals(user.getId())) {
                    UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                            groupChat.getId(), member.getUserId()
                    );
                    unreadMessage.incrementUnreadCount();
                    cassandraUnreadMessageDomainService.save(unreadMessage);
                }
            }

        } else {
            throw new RuntimeException(
                "Chat type is unknown or an error occurred while incrementing the unread message counter"
            );
        }
    }

    public void decrementUnreadMessageCountForOther(@NonNull ChatInterface chatInterface, @NonNull User user) {
        if (chatInterface instanceof PrivateChat privateChat) {

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    chatInterface.getId(), privateChat.getCompanionUserId(user.getId())
            );
            unreadMessage.decrementUnreadCount();
            cassandraUnreadMessageDomainService.save(unreadMessage);

        } else if (chatInterface instanceof GroupChat groupChat) {

            List<ChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
            for (ChatMember member : members) {
                if (!member.getUserId().equals(user.getId())) {
                    UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                            chatInterface.getId(), member.getUserId()
                    );
                    unreadMessage.decrementUnreadCount();
                    cassandraUnreadMessageDomainService.save(unreadMessage);
                }
            }

        } else {
            throw new RuntimeException(
                "Chat type is unknown or an error occurred while decrementing the unread message counter"
            );
        }
    }

    public void incrementUnreadMessageCountForUser(@NonNull ChatInterface chatInterface, @NonNull User user) {
        final UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                chatInterface.getId(), user.getId()
        );
        unreadMessage.incrementUnreadCount();
        cassandraUnreadMessageDomainService.save(unreadMessage);
    }

    public void decrementUnreadMessageCountForUser(@NonNull ChatInterface chatInterface, @NonNull User user) {
        final UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                chatInterface.getId(), user.getId()
        );
        unreadMessage.decrementUnreadCount();
        cassandraUnreadMessageDomainService.save(unreadMessage);
    }

}
