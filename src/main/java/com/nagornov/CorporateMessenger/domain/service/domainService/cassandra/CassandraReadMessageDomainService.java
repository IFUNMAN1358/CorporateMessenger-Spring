package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraReadMessageByIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraReadMessageByMessageIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraReadMessageDomainService {

    private final CassandraReadMessageByIdRepository cassandraReadMessageByIdRepository;
    private final CassandraReadMessageByMessageIdRepository cassandraReadMessageByMessageIdRepository;

    public void save(@NotNull ReadMessage readMessage) {
        cassandraReadMessageByIdRepository.findById(readMessage.getId())
                .ifPresent(e -> {
                    throw new ResourceConflictException("Read message already exists during save");
                });
        cassandraReadMessageByIdRepository.saveWithoutCheck(readMessage);
        cassandraReadMessageByMessageIdRepository.saveWithoutCheck(readMessage);
    }

    public void update(@NotNull ReadMessage readMessage) {
        cassandraReadMessageByIdRepository.findById(readMessage.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Read message not found during update"));
        cassandraReadMessageByIdRepository.updateWithoutCheck(readMessage);
        cassandraReadMessageByMessageIdRepository.updateWithoutCheck(readMessage);
    }

    public void delete(@NotNull ReadMessage readMessage) {
        cassandraReadMessageByIdRepository.findById(readMessage.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Read message not found during delete"));
        cassandraReadMessageByIdRepository.deleteWithoutCheck(readMessage);
        cassandraReadMessageByMessageIdRepository.deleteWithoutCheck(readMessage);
    }

    public ReadMessage getById(@NotNull UUID id) {
        return cassandraReadMessageByIdRepository.findById(id)
                       .orElseThrow(() -> new ResourceNotFoundException("Read message with this id not found"));
    }

    public List<ReadMessage> getAllByMessageId(@NotNull UUID messageId) {
        return cassandraReadMessageByMessageIdRepository.getAllByMessageId(messageId);
    }

    public Boolean checkExistsByMessageIdAndUserId(@NotNull UUID messageId, @NotNull UUID userId) {
        return cassandraReadMessageByMessageIdRepository
                .getAllByMessageId(messageId)
                .stream()
                .anyMatch(readMessage -> readMessage.getUserId().equals(userId));
    }

}
