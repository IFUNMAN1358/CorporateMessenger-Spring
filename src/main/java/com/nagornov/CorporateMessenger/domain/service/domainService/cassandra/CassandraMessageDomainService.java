package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageByIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraMessageDomainService {

    private final CassandraMessageByIdRepository cassandraMessageByIdRepository;
    private final CassandraMessageByChatIdRepository cassandraMessageByChatIdRepository;

    public void save(@NotNull Message message) {
        cassandraMessageByIdRepository.findById(message.getId())
                .ifPresent(_ -> {
                    throw new ResourceConflictException("Message already exists during save");
                });
        cassandraMessageByIdRepository.saveWithoutCheck(message);
        cassandraMessageByChatIdRepository.saveWithoutCheck(message);
    }

    public void update(@NotNull Message message) {
        cassandraMessageByIdRepository.findById(message.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Message not found during update"));
        cassandraMessageByIdRepository.updateWithoutCheck(message);
        cassandraMessageByChatIdRepository.updateWithoutCheck(message);
    }

    public void delete(@NotNull Message message) {
        cassandraMessageByIdRepository.findById(message.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Message not found during delete"));
        cassandraMessageByIdRepository.deleteWithoutCheck(message);
        cassandraMessageByChatIdRepository.deleteWithoutCheck(message);
    }

    public Message getById(@NotNull UUID id) {
        return cassandraMessageByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message with this id not found"));
    }

    public Optional<Message> findById(@NotNull UUID id) {
        return cassandraMessageByIdRepository.findById(id);
    }

    public Optional<Message> findLastByChatId(@NotNull UUID chatId) {
        return cassandraMessageByChatIdRepository.findLastByChatId(chatId);
    }

    public List<Message> getAllByChatId(@NotNull UUID chatId, @NotNull int page, @NotNull int size) {
        return cassandraMessageByChatIdRepository
                .getAllByChatId(chatId, page, size);
    }

}
