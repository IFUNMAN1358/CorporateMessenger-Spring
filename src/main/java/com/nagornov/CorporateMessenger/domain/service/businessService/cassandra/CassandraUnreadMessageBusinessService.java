package com.nagornov.CorporateMessenger.domain.service.businessService.cassandra;

import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
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

    public void incrementUnreadMessageCountForOther(@NonNull Chat chat, @NonNull User user) {
        if (chat instanceof PrivateChat privateChat) {

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    privateChat.getId(), privateChat.getCompanionUserId(user.getId())
            );
            unreadMessage.incrementUnreadCount();
            cassandraUnreadMessageDomainService.save(unreadMessage);

        } else if (chat instanceof GroupChat groupChat) {

            List<GroupChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
            for (GroupChatMember member : members) {
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

    public void decrementUnreadMessageCountForOther(@NonNull Chat chat, @NonNull User user) {
        if (chat instanceof PrivateChat privateChat) {

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    chat.getId(), privateChat.getCompanionUserId(user.getId())
            );
            unreadMessage.decrementUnreadCount();
            cassandraUnreadMessageDomainService.save(unreadMessage);

        } else if (chat instanceof GroupChat groupChat) {

            List<GroupChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
            for (GroupChatMember member : members) {
                if (!member.getUserId().equals(user.getId())) {
                    UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                            chat.getId(), member.getUserId()
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

    public void incrementUnreadMessageCountForUser(@NonNull Chat chat, @NonNull User user) {
        final UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                chat.getId(), user.getId()
        );
        unreadMessage.incrementUnreadCount();
        cassandraUnreadMessageDomainService.save(unreadMessage);
    }

    public void decrementUnreadMessageCountForUser(@NonNull Chat chat, @NonNull User user) {
        final UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                chat.getId(), user.getId()
        );
        unreadMessage.decrementUnreadCount();
        cassandraUnreadMessageDomainService.save(unreadMessage);
    }

}
