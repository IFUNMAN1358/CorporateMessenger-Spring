package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisMessageEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.utils.RedisKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisMessageRepository {

    private final RedisTemplate<String, RedisMessageEntity> redisMessageTemplate;
    private final RedisMessageMapper redisMessageMapper;

    public void leftSave(Long chatId, Message message, long timeout, TimeUnit unit) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        redisMessageTemplate.opsForList().leftPush(
                messageKey,
                redisMessageMapper.toEntity(message)
        );
        redisMessageTemplate.expire(messageKey, timeout, unit);
    }

    public void rightSaveAll(Long chatId, List<Message> messages, long timeout, TimeUnit unit) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        redisMessageTemplate.opsForList().rightPushAll(
                messageKey,
                messages.stream().map(redisMessageMapper::toEntity).toList()
        );
        redisMessageTemplate.expire(messageKey, timeout, unit);
    }

    public void update(Long chatId, Message message) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        List<RedisMessageEntity> messages = redisMessageTemplate.opsForList().range(messageKey, 0, -1);

        if (messages == null) {
            throw new RuntimeException("Message in redis not found by (key)chatId=%s for update".formatted(chatId));
        }

        int index = -1;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                index = i;
                break;
            }
        }

        redisMessageTemplate.opsForList().set(
                messageKey,
                index,
                redisMessageMapper.toEntity(message)
        );
    }

    public void delete(Long chatId, Message message) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        redisMessageTemplate.opsForList().remove(
                messageKey,
                1,
                redisMessageMapper.toEntity(message)
        );
    }

    public List<Message> findAll(Long chatId, int page, int size) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        int start = page * size;
        int end = start + size - 1;

        List<RedisMessageEntity> messages = redisMessageTemplate.opsForList().range(messageKey, start, end);

        if (messages == null) {
            return List.of();
        }

        return messages.stream().map(redisMessageMapper::toDomain).toList();
    }

}
