package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.enums.redis.RedisPrefix;
import com.nagornov.CorporateMessenger.domain.model.user.UserBlacklist;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisUserBlacklistEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisUserBlacklistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisUserBlacklistRepository {

    private static final String USER_BLACKLIST_PREFIX = RedisPrefix.USER_BLACKLIST.getPrefix();
    private static final long TIMEOUT = 24;
    private static final TimeUnit TIME_UNIT = TimeUnit.HOURS;

    private final RedisTemplate<String, RedisUserBlacklistEntity> redisUserBlacklistTemplate;
    private final RedisUserBlacklistMapper redisUserBlacklistMapper;


    public void saveUserBlacklistToSet(UserBlacklist userBlacklist) {
        String key = "%s:%s".formatted(USER_BLACKLIST_PREFIX, userBlacklist.getUserId());
        redisUserBlacklistTemplate.opsForSet().add(
                key,
                redisUserBlacklistMapper.toEntity(userBlacklist)
        );
        redisUserBlacklistTemplate.expire(key, TIMEOUT, TIME_UNIT);
    }

    public void saveAllUserBlacklistsToSet(UUID userId, List<UserBlacklist> userBlacklists) {
        String key = "%s:%s".formatted(USER_BLACKLIST_PREFIX, userId);
        redisUserBlacklistTemplate.opsForSet().add(
                key,
                userBlacklists.stream()
                        .map(redisUserBlacklistMapper::toEntity)
                        .toArray(RedisUserBlacklistEntity[]::new)
        );
        redisUserBlacklistTemplate.expire(key, TIMEOUT, TIME_UNIT);
    }

    public void deleteUserBlacklistFromSet(UserBlacklist userBlacklist) {
        redisUserBlacklistTemplate.opsForSet().remove(
                "%s:%s".formatted(USER_BLACKLIST_PREFIX, userBlacklist.getUserId()),
                redisUserBlacklistMapper.toEntity(userBlacklist)
        );
    }

    public void deleteUserBlacklistFromSetByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        String key = "%s:%s".formatted(USER_BLACKLIST_PREFIX, userId);
        Set<RedisUserBlacklistEntity> set = redisUserBlacklistTemplate.opsForSet().members(key);

        if (set == null || set.isEmpty()) {
            return;
        }

        for (RedisUserBlacklistEntity entity : set) {
            if (entity.getBlockedUserId().equals(blockedUserId)) {
                redisUserBlacklistTemplate.opsForSet().remove(key, entity);
                return;
            }
        }
    }

    public List<UserBlacklist> findAllUserBlacklistsInSetByUserId(UUID userId, int page, int size) {
        Set<RedisUserBlacklistEntity> set = redisUserBlacklistTemplate.opsForSet().members(
                "%s:%s".formatted(USER_BLACKLIST_PREFIX, userId)
        );

        if (set == null || set.isEmpty()) {
            return List.of();
        }

        List<UserBlacklist> userBlacklists = set.stream().map(redisUserBlacklistMapper::toDomain)
                .sorted(Comparator.comparing(UserBlacklist::getBlockedAt))
                .toList();

        int fromIndex = page * size;
        int toIndex = Math.min((page + 1) * size, userBlacklists.size());

        return userBlacklists.subList(fromIndex, toIndex);
    }

    public Optional<UserBlacklist> findUserBlacklistInSetByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        Set<RedisUserBlacklistEntity> set = redisUserBlacklistTemplate.opsForSet().members(
                "%s:%s".formatted(USER_BLACKLIST_PREFIX, userId)
        );

        if (set == null || set.isEmpty()) {
            return Optional.empty();
        }

        return set.stream().filter(e -> e.getBlockedUserId().equals(blockedUserId)).findFirst()
                .map(redisUserBlacklistMapper::toDomain);
    }

    public boolean existsUserBlacklistInSetByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        Set<RedisUserBlacklistEntity> set = redisUserBlacklistTemplate.opsForSet().members(
                "%s:%s".formatted(USER_BLACKLIST_PREFIX, userId)
        );

        if (set == null || set.isEmpty()) {
            return false;
        }

        return set.stream().anyMatch(e -> e.getBlockedUserId().equals(blockedUserId));
    }

    //
    //
    //

    public void saveNegativeKeyToHash(UUID userId, UUID blockedUserId) {
        String negativeKey = "%s:negative:%s:%s".formatted(USER_BLACKLIST_PREFIX, userId, blockedUserId);
        redisUserBlacklistTemplate.opsForHash().put("%s:negative".formatted(USER_BLACKLIST_PREFIX), negativeKey, "NOT_BLOCKED");
    }

    public void saveNegativeKeyToHashByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        String negativeKey = "%s:negative:%s:%s".formatted(USER_BLACKLIST_PREFIX, userId, blockedUserId);
        redisUserBlacklistTemplate.opsForHash().put("%s:negative".formatted(USER_BLACKLIST_PREFIX), negativeKey, "NOT_BLOCKED");
    }

    public void deleteNegativeKeyFromHash(UUID userId, UUID blockedUserId) {
        String negativeKey = "%s:negative:%s:%s".formatted(USER_BLACKLIST_PREFIX, userId, blockedUserId);
        redisUserBlacklistTemplate.opsForHash().delete("%s:negative".formatted(USER_BLACKLIST_PREFIX), negativeKey);
    }

    public void deleteAllNegativeKeysFromHash(UUID userId, List<UserBlacklist> userBlacklists) {
        List<String> negativeKeys = userBlacklists.stream().map(
                ub -> "%s:negative:%s:%s".formatted(USER_BLACKLIST_PREFIX, userId, ub.getBlockedUserId())
        ).toList();
        redisUserBlacklistTemplate.opsForHash().delete("%s:negative".formatted(USER_BLACKLIST_PREFIX), negativeKeys);
    }

    public boolean existsNegativeKeyInHashByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        String negativeKey = "%s:negative:%s:%s".formatted(USER_BLACKLIST_PREFIX, userId, blockedUserId);
        return redisUserBlacklistTemplate.opsForHash().hasKey("%s:negative".formatted(USER_BLACKLIST_PREFIX), negativeKey);
    }

}
