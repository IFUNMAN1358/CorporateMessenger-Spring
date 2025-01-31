package com.nagornov.CorporateMessenger.domain.service.businessService.cassandra;

import com.nagornov.CorporateMessenger.domain.model.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraUnreadMessageDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CassandraUnreadMessageBusinessService {

    private final CassandraUnreadMessageDomainService cassandraUnreadMessageDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;

    public void incrementUnreadMessageCountForOther(@NotNull Chat chat, @NotNull User user) {
        if (chat instanceof PrivateChat privateChat) {

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    privateChat.getId(), privateChat.getCompanionUserId(user.getId())
            );
            unreadMessage.incrementUnreadCount();
            cassandraUnreadMessageDomainService.update(unreadMessage);

        } else if (chat instanceof GroupChat groupChat) {

            List<GroupChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
            for (GroupChatMember member : members) {
                if (!member.getUserId().equals(user.getId())) {
                    UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                            groupChat.getId(), member.getUserId()
                    );
                    unreadMessage.incrementUnreadCount();
                    cassandraUnreadMessageDomainService.update(unreadMessage);
                }
            }

        } else {
            throw new RuntimeException(
                "Chat type is unknown or an error occurred while incrementing the unread message counter"
            );
        }
    }

    public void decrementUnreadMessageCountForOther(@NotNull Chat chat, @NotNull User user) {
        if (chat instanceof PrivateChat privateChat) {

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    chat.getId(), privateChat.getCompanionUserId(user.getId())
            );
            unreadMessage.decrementUnreadCount();
            cassandraUnreadMessageDomainService.update(unreadMessage);

        } else if (chat instanceof GroupChat groupChat) {

            List<GroupChatMember> members = cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId());
            for (GroupChatMember member : members) {
                if (!member.getUserId().equals(user.getId())) {
                    UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                            chat.getId(), member.getUserId()
                    );
                    unreadMessage.decrementUnreadCount();
                    cassandraUnreadMessageDomainService.update(unreadMessage);
                }
            }

        } else {
            throw new RuntimeException(
                "Chat type is unknown or an error occurred while decrementing the unread message counter"
            );
        }
    }

    public void incrementUnreadMessageCountForUser(@NotNull Chat chat, @NotNull User user) {
        final UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                chat.getId(), user.getId()
        );
        unreadMessage.incrementUnreadCount();
        cassandraUnreadMessageDomainService.update(unreadMessage);
    }

    public void decrementUnreadMessageCountForUser(@NotNull Chat chat, @NotNull User user) {
        final UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                chat.getId(), user.getId()
        );
        unreadMessage.decrementUnreadCount();
        cassandraUnreadMessageDomainService.update(unreadMessage);
    }

}
