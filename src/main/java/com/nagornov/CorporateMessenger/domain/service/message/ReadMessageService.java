package com.nagornov.CorporateMessenger.domain.service.message;

import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraReadMessageRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadMessageService {

    private final CassandraReadMessageRepository cassandraReadMessageRepository;

    public ReadMessage create(@NonNull UUID userId, @NonNull UUID messageId) {
        return cassandraReadMessageRepository.save(
                new ReadMessage(
                        UUID.randomUUID(),
                        userId,
                        messageId
                )
        );
    }

    public void delete(@NonNull ReadMessage readMessage) {
        cassandraReadMessageRepository.delete(readMessage);
    }

    public void deleteAllByMessageId(@NonNull UUID messageId) {
        cassandraReadMessageRepository.deleteAllByMessageId(messageId);
    }

    public List<ReadMessage> findAllByMessageId(@NonNull UUID messageId) {
        return cassandraReadMessageRepository.findAllByMessageId(messageId);
    }

    public boolean existsByMessageIdAndUserId(@NonNull UUID messageId, @NonNull UUID userId) {
        return cassandraReadMessageRepository
                .findAllByMessageId(messageId)
                .stream()
                .anyMatch(readMessage -> readMessage.getUserId().equals(userId));
    }

}
