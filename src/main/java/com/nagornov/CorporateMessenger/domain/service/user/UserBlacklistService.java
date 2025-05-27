package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.UserBlacklist;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserBlacklistRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisUserBlacklistRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserBlacklistService {

    private final JpaUserBlacklistRepository jpaUserBlacklistRepository;
    private final RedisUserBlacklistRepository redisUserBlacklistRepository;

    @Transactional
    public UserBlacklist create(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        UserBlacklist userBlacklist = new UserBlacklist(
                UUID.randomUUID(),
                userId,
                blockedUserId,
                Instant.now()
        );
        redisUserBlacklistRepository.deleteNegativeKeyFromHash(userId, blockedUserId);
        redisUserBlacklistRepository.saveUserBlacklistToSet(userBlacklist);
        return jpaUserBlacklistRepository.save(userBlacklist);
    }

    @Transactional
    public void delete(@NonNull UserBlacklist userBlacklist) {
        redisUserBlacklistRepository.saveNegativeKeyToHash(userBlacklist.getUserId(), userBlacklist.getBlockedUserId());
        redisUserBlacklistRepository.deleteUserBlacklistFromSet(userBlacklist);
        jpaUserBlacklistRepository.delete(userBlacklist);
    }

    @Transactional
    public void deleteByUserIdAndBlockedUserId(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        try {
            redisUserBlacklistRepository.saveNegativeKeyToHashByUserIdAndBlockedUserId(userId, blockedUserId);
            redisUserBlacklistRepository.deleteUserBlacklistFromSetByUserIdAndBlockedUserId(userId, blockedUserId);
            jpaUserBlacklistRepository.deleteByUserIdAndBlockedUserId(userId, blockedUserId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    "UserBlacklist[userId=%s, blockedUserId=%s] not found for deleting"
                            .formatted(userId, blockedUserId)
            );
        }
    }

    public List<UserBlacklist> findAllByUserId(@NonNull UUID userId, int page, int size) {
        try {
            List<UserBlacklist> userBlacklists = redisUserBlacklistRepository.findAllUserBlacklistsInSetByUserId(userId, page, size);
            if (userBlacklists.isEmpty()) {
                userBlacklists = jpaUserBlacklistRepository.findAllByUserId(userId, page, size).toList();
                redisUserBlacklistRepository.deleteAllNegativeKeysFromHash(userId, userBlacklists);
                redisUserBlacklistRepository.saveAllUserBlacklistsToSet(userId, userBlacklists);
            }
            return userBlacklists;
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean existsByUserIdAndBlockedUserId(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        boolean isNegativeKeyExists =
                redisUserBlacklistRepository.existsNegativeKeyInHashByUserIdAndBlockedUserId(userId, blockedUserId);
        if (isNegativeKeyExists) {
            return false;
        }

        boolean isRedisUBExists = redisUserBlacklistRepository.existsUserBlacklistInSetByUserIdAndBlockedUserId(userId, blockedUserId);

        if (!isRedisUBExists) {
            Optional<UserBlacklist> optPostgresUB = jpaUserBlacklistRepository.findByUserIdAndBlockedUserId(userId, blockedUserId);

            if (optPostgresUB.isPresent()) {
                redisUserBlacklistRepository.saveUserBlacklistToSet(optPostgresUB.get());
                return true;
            }
            redisUserBlacklistRepository.saveNegativeKeyToHash(userId, blockedUserId);
            return false;
        }
        return true;
    }

    public void ensureAnyMatchBetweenUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        if (
                existsByUserIdAndBlockedUserId(userId1, userId2)
                ||
                existsByUserIdAndBlockedUserId(userId2, userId1)
        ) {
            throw new ResourceConflictException("This user has blocked you or you have blocked this user");
        }
    }

    public void ensureUserNotBlocked(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        if (existsByUserIdAndBlockedUserId(userId, blockedUserId)) {
            throw new ResourceBadRequestException("User has blocked you");
        }
    }

}
