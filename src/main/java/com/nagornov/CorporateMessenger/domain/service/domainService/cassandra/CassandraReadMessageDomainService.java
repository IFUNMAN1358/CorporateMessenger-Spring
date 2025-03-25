package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraReadMessageByIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraReadMessageByMessageIdRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraReadMessageDomainService {

    private final CassandraReadMessageByIdRepository cassandraReadMessageByIdRepository;
    private final CassandraReadMessageByMessageIdRepository cassandraReadMessageByMessageIdRepository;

    public ReadMessage save(@NonNull ReadMessage readMessage) {
        cassandraReadMessageByIdRepository.save(readMessage);
        return cassandraReadMessageByMessageIdRepository.save(readMessage);
    }

    public void delete(@NonNull ReadMessage readMessage) {
        cassandraReadMessageByIdRepository.delete(readMessage);
        cassandraReadMessageByMessageIdRepository.delete(readMessage);
    }

    public ReadMessage getById(@NonNull UUID id) {
        return cassandraReadMessageByIdRepository.findById(id)
                       .orElseThrow(() -> new ResourceNotFoundException("Read message with this id not found"));
    }

    public List<ReadMessage> getAllByMessageId(@NonNull UUID messageId) {
        return cassandraReadMessageByMessageIdRepository.getAllByMessageId(messageId);
    }

    public Boolean checkExistsByMessageIdAndUserId(@NonNull UUID messageId, @NonNull UUID userId) {
        return cassandraReadMessageByMessageIdRepository
                .getAllByMessageId(messageId)
                .stream()
                .anyMatch(readMessage -> readMessage.getUserId().equals(userId));
    }

}
