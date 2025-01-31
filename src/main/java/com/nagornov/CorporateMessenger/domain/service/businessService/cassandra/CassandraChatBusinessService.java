package com.nagornov.CorporateMessenger.domain.service.businessService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.*;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatMemberDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraGroupChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraPrivateChatDomainService;
import jakarta.validation.constraints.NotNull;
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

    public Chat getAvailableById(@NotNull UUID id) {

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

    public void update(@NotNull Chat chat) {
        if (chat instanceof PrivateChat privateChat) {
            cassandraPrivateChatDomainService.update(privateChat);
        } else if (chat instanceof GroupChat groupChat) {
            cassandraGroupChatDomainService.update(groupChat);
        } else {
            throw new RuntimeException("Chat type is unknown or there was an error updating the chat");
        }
    }

    public void validateUserOwnership(@NotNull Chat chat, @NotNull User user) {
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
