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

    public void leftSave(UUID chatId, Message message) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        redisMessageTemplate.opsForList().leftPush(
                messageKey,
                redisMessageMapper.toEntity(message)
        );
    }

    public void rightSaveAll(UUID chatId, List<Message> messages, long timeout, TimeUnit unit) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        redisMessageTemplate.opsForList().rightPushAll(
                messageKey,
                messages.stream().map(redisMessageMapper::toEntity).toList()
        );
        redisMessageTemplate.expire(messageKey, timeout, unit);
    }

    public List<Message> getAll(UUID chatId, int page, int size) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        int start = page * size;
        int end = start + size - 1;

        return redisMessageTemplate.opsForList().range(messageKey, start, end)
                .stream().map(redisMessageMapper::toDomain).toList();
    }

    public void update(UUID chatId, Message message) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        List<RedisMessageEntity> messages = redisMessageTemplate.opsForList().range(messageKey, 0, -1);

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

    public void delete(UUID chatId, Message message) {
        String messageKey = RedisKeyUtils.messageKey(chatId);

        redisMessageTemplate.opsForList().remove(
                messageKey,
                1,
                redisMessageMapper.toEntity(message)
        );
    }

}
