package com.nagornov.CorporateMessenger.domain.service.message;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
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

    public ReadMessage save(@NonNull ReadMessage readMessage) {
        return cassandraReadMessageRepository.save(readMessage);
    }

    public void delete(@NonNull ReadMessage readMessage) {
        cassandraReadMessageRepository.delete(readMessage);
    }

    public ReadMessage getById(@NonNull UUID id) {
        return cassandraReadMessageRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Read message with this id not found"));
    }

    public List<ReadMessage> getAllByMessageId(@NonNull UUID messageId) {
        return cassandraReadMessageRepository.getAllByMessageId(messageId);
    }

    public Boolean checkExistsByMessageIdAndUserId(@NonNull UUID messageId, @NonNull UUID userId) {
        return cassandraReadMessageRepository
                .getAllByMessageId(messageId)
                .stream()
                .anyMatch(readMessage -> readMessage.getUserId().equals(userId));
    }

}
