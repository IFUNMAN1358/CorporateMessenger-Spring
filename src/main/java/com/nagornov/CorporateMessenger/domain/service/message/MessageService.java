package com.nagornov.CorporateMessenger.domain.service.message;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisMessageRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final CassandraMessageRepository cassandraMessageRepository;
    private final RedisMessageRepository redisMessageRepository;

    //
    // CASSANDRA
    //

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

    //
    // REDIS
    //

    public void leftSaveToRedis(
            @NonNull UUID chatId,
            @NonNull Message message,
            long timeout,
            @NonNull TimeUnit unit
    ) {
        try {
            redisMessageRepository.leftSave(chatId, message, timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("Error while left saving message to Redis", e);
        }
    }

    public void rightSaveAllToRedis(
            @NonNull UUID chatId,
            @NonNull List<Message> messages,
            long timeout,
            @NonNull TimeUnit unit
    ) {
        try {
            redisMessageRepository.rightSaveAll(chatId, messages, timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("Error while right saving messages to Redis", e);
        }
    }

    public List<Message> findAllFromRedis(@NonNull UUID chatId, int page, int size) {
        try {
            return redisMessageRepository.findAll(chatId, page, size);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting messages from Redis", e);
        }
    }

    public void updateToRedis(@NonNull UUID chatId, @NonNull Message message) {
        try {
            redisMessageRepository.update(chatId, message);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating message in Redis", e);
        }
    }

    public void deleteFromRedis(@NonNull UUID chatId, @NonNull Message message) {
        try {
            redisMessageRepository.delete(chatId, message);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting message from Redis", e);
        }
    }

}
