package com.nagornov.CorporateMessenger.domain.service.message;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisMessageRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final CassandraMessageRepository cassandraMessageRepository;
    private final RedisMessageRepository redisMessageRepository;

    public Message create(
            @NonNull Long chatId,
            @NonNull UUID senderId,
            @NonNull String senderUsername,
            @NonNull String content,
            boolean hasFiles
    ) {
        Message message = new Message(
                Uuids.timeBased(),
                chatId,
                senderId,
                senderUsername,
                content,
                hasFiles,
                false,
                false,
                Instant.now()
        );
        return cassandraMessageRepository.save(message);
    }

    public Message update(@NonNull Message message) {
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

    public List<Message> findAllByChatId(@NonNull Long chatId, int page, int size) {
        return cassandraMessageRepository
                .getAllByChatId(chatId, page, size);
    }

    public Message getByChatIdAndId(@NonNull Long chatId, @NonNull UUID id) {
        return cassandraMessageRepository.findByChatIdAndId(chatId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Message[chatId=%s, id=%s] not found".formatted(chatId, id)));
    }

    public void leftSaveToRedis(
            @NonNull Long chatId,
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
            @NonNull Long chatId,
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

    public List<Message> findAllFromRedis(@NonNull Long chatId, int page, int size) {
        try {
            return redisMessageRepository.findAll(chatId, page, size);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting messages from Redis", e);
        }
    }

    public void updateToRedis(@NonNull Long chatId, @NonNull Message message) {
        try {
            redisMessageRepository.update(chatId, message);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating message in Redis", e);
        }
    }

    public void deleteFromRedis(@NonNull Long chatId, @NonNull Message message) {
        try {
            redisMessageRepository.delete(chatId, message);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting message from Redis", e);
        }
    }

}
