package com.nagornov.CorporateMessenger.domain.service;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final CassandraMessageRepository cassandraMessageRepository;

    public Message save(@NonNull Message message) {
        return cassandraMessageRepository.save(message);
    }

    public void delete(@NonNull Message message) {
        cassandraMessageRepository.delete(message);
    }

    public Message getById(@NonNull UUID id) {
        return cassandraMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message with this id not found"));
    }

    public Optional<Message> findById(@NonNull UUID id) {
        return cassandraMessageRepository.findById(id);
    }

    public Optional<Message> findLastByChatId(@NonNull Long chatId) {
        return cassandraMessageRepository.findLastByChatId(chatId);
    }

    public List<Message> getAllByChatId(@NonNull Long chatId, int page, int size) {
        return cassandraMessageRepository
                .getAllByChatId(chatId, page, size);
    }

}
