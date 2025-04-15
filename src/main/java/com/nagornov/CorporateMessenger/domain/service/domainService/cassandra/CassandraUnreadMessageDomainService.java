package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraUnreadMessageRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraUnreadMessageDomainService {

    private final CassandraUnreadMessageRepository cassandraUnreadMessageRepository;

    public UnreadMessage save(@NonNull UnreadMessage unreadMessage) {
        return cassandraUnreadMessageRepository.save(unreadMessage);
    }

    public void delete(@NonNull UnreadMessage unreadMessage) {
        cassandraUnreadMessageRepository.delete(unreadMessage);
    }

    public UnreadMessage getByChatIdAndUserId(@NonNull UUID chatId, @NonNull UUID userId) {
        return cassandraUnreadMessageRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Unread message with chatId and userId not found"));
    }

}
