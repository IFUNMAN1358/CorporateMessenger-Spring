package com.nagornov.CorporateMessenger.domain.service.domainService.redis;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisMessageRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisMessageDomainService {

    private final RedisMessageRepository redisMessageRepository;

    public void leftSave(
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

    public void rightSaveAll(
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

    public List<Message> findAll(@NonNull UUID chatId, int page, int size) {
        try {
            return redisMessageRepository.findAll(chatId, page, size);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting messages from Redis", e);
        }
    }

    public void update(@NonNull UUID chatId, @NonNull Message message) {
        try {
            redisMessageRepository.update(chatId, message);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating message in Redis", e);
        }
    }

    public void delete(@NonNull UUID chatId, @NonNull Message message) {
        try {
            redisMessageRepository.delete(chatId, message);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting message from Redis", e);
        }
    }

}
