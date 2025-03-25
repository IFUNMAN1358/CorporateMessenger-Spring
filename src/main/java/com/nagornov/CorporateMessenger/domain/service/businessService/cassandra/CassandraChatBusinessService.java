package com.nagornov.CorporateMessenger.domain.service.businessService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraPrivateChatDomainService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraChatBusinessService {

    private final CassandraPrivateChatDomainService cassandraPrivateChatDomainService;
    private final CassandraGroupChatDomainService cassandraGroupChatDomainService;
    private final CassandraGroupChatMemberDomainService cassandraGroupChatMemberDomainService;

    public Chat getAvailableById(@NonNull UUID id) {

        Optional<PrivateChat> privateChat = cassandraPrivateChatDomainService.findAvailableById(id);
        if (privateChat.isPresent()) {
            return privateChat.get();
        }

        Optional<GroupChat> groupChat = cassandraGroupChatDomainService.findById(id);
        if (groupChat.isPresent()) {
            return groupChat.get();
        }

        throw new ResourceNotFoundException("Chat with this id does not exist: " + id);
    }

    public void update(@NonNull Chat chat) {
        if (chat instanceof PrivateChat privateChat) {
            cassandraPrivateChatDomainService.save(privateChat);
        } else if (chat instanceof GroupChat groupChat) {
            cassandraGroupChatDomainService.save(groupChat);
        } else {
            throw new RuntimeException("Chat type is unknown or there was an error updating the chat");
        }
    }

    public void validateUserOwnership(@NonNull Chat chat, @NonNull User user) {
        if (chat instanceof PrivateChat privateChat) {
            privateChat.validateUserIdOwnership(user.getId());
        } else if (chat instanceof GroupChat groupChat) {
            cassandraGroupChatMemberDomainService.validateUserOwnership(groupChat.getId(), user.getId());
        } else {
            throw new RuntimeException(
                "Chat type is unknown or an error occurred while checking the user's relationship with the chat"
            );
        }
    }

}
