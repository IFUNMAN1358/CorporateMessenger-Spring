package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.enums.redis.RedisPrefix;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisMessageEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisMessageRepository {

    private final static String MESSAGE = RedisPrefix.MESSAGE.getPrefix();

    private final RedisTemplate<String, RedisMessageEntity> redisMessageTemplate;
    private final RedisMessageMapper redisMessageMapper;

    public void leftSave(Long chatId, Message message, long timeout, TimeUnit unit) {
        String key = "%s:%s".formatted(MESSAGE, chatId);

        redisMessageTemplate.opsForList().leftPush(
                key,
                redisMessageMapper.toEntity(message)
        );
        redisMessageTemplate.expire(key, timeout, unit);
    }

    public void rightSaveAll(Long chatId, List<Message> messages, long timeout, TimeUnit unit) {
        String key = "%s:%s".formatted(MESSAGE, chatId);

        redisMessageTemplate.opsForList().rightPushAll(
                key,
                messages.stream().map(redisMessageMapper::toEntity).toList()
        );
        redisMessageTemplate.expire(key, timeout, unit);
    }

    public void update(Long chatId, Message message) {
        String key = "%s:%s".formatted(MESSAGE, chatId);

        List<RedisMessageEntity> messages = redisMessageTemplate.opsForList().range(key, 0, -1);

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
                key,
                index,
                redisMessageMapper.toEntity(message)
        );
    }

    public void delete(Long chatId, Message message) {
        String key = "%s:%s".formatted(MESSAGE, chatId);

        Boolean keyExists = redisMessageTemplate.hasKey(key);
        if (Boolean.FALSE.equals(keyExists)) {
            return;
        }

        List<RedisMessageEntity> messages = redisMessageTemplate.opsForList().range(key, 0, -1);
        if (messages == null || messages.isEmpty()) {
            return;
        }

        int index = -1;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return;
        }

        redisMessageTemplate.opsForList().remove(key, 1, messages.get(index));

        Long size = redisMessageTemplate.opsForList().size(key);
        if (size != null && size == 0) {
            redisMessageTemplate.delete(key);
        }
    }

    public List<Message> findAll(Long chatId, int page, int size) {
        String key = "%s:%s".formatted(MESSAGE, chatId);

        int start = page * size;
        int end = start + size - 1;

        List<RedisMessageEntity> messages = redisMessageTemplate.opsForList().range(key, start, end);

        if (messages == null) {
            return List.of();
        }

        return messages.stream().map(redisMessageMapper::toDomain).toList();
    }

}
