package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageByIdRepository;
import lombok.NonNull;
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

    public Message save(@NonNull Message message) {
        cassandraMessageByIdRepository.save(message);
        return cassandraMessageByChatIdRepository.save(message);
    }

    public void delete(@NonNull Message message) {
        cassandraMessageByIdRepository.delete(message);
        cassandraMessageByChatIdRepository.delete(message);
    }

    public Message getById(@NonNull UUID id) {
        return cassandraMessageByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message with this id not found"));
    }

    public Optional<Message> findById(@NonNull UUID id) {
        return cassandraMessageByIdRepository.findById(id);
    }

    public Optional<Message> findLastByChatId(@NonNull UUID chatId) {
        return cassandraMessageByChatIdRepository.findLastByChatId(chatId);
    }

    public List<Message> getAllByChatId(@NonNull UUID chatId, int page, int size) {
        return cassandraMessageByChatIdRepository
                .getAllByChatId(chatId, page, size);
    }

}
