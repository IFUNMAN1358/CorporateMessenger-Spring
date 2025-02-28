package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.UnreadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraUnreadMessageByChatIdAndUserIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraUnreadMessageDomainService {

    private final CassandraUnreadMessageByChatIdAndUserIdRepository cassandraUnreadMessageByChatIdAndUserIdRepository;

    public void save(@NotNull UnreadMessage unreadMessage) {
        cassandraUnreadMessageByChatIdAndUserIdRepository
                .findByChatIdAndUserId(unreadMessage.getChatId(), unreadMessage.getUserId())
                        .ifPresent(_ -> {
                            throw new ResourceConflictException("Unread message already exists during save");
                        });
        cassandraUnreadMessageByChatIdAndUserIdRepository.saveWithoutCheck(unreadMessage);
    }

    public void update(@NotNull UnreadMessage unreadMessage) {
        cassandraUnreadMessageByChatIdAndUserIdRepository
                .findByChatIdAndUserId(unreadMessage.getChatId(), unreadMessage.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Unread message not found during update"));
        cassandraUnreadMessageByChatIdAndUserIdRepository.updateWithoutCheck(unreadMessage);
    }

    public void delete(@NotNull UnreadMessage unreadMessage) {
        cassandraUnreadMessageByChatIdAndUserIdRepository
                .findByChatIdAndUserId(unreadMessage.getChatId(), unreadMessage.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Unread message not found during delete"));
        cassandraUnreadMessageByChatIdAndUserIdRepository.deleteWithoutCheck(unreadMessage);
    }

    public UnreadMessage getByChatIdAndUserId(@NotNull UUID chatId, @NotNull UUID userId) {
        return cassandraUnreadMessageByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Unread message with chatId and userId not found"));
    }

}
