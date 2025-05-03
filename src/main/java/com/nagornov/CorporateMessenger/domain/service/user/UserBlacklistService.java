package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.UserBlacklist;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserBlacklistRepository;
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

    //
    // JPA
    //

    @Transactional
    public UserBlacklist create(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        UserBlacklist userBlacklist = new UserBlacklist(
                UUID.randomUUID(),
                userId,
                blockedUserId,
                Instant.now()
        );
        return jpaUserBlacklistRepository.save(userBlacklist);
    }

    @Transactional
    public void delete(@NonNull UserBlacklist userBlacklist) {
        jpaUserBlacklistRepository.delete(userBlacklist);
    }

    @Transactional
    public void deleteById(@NonNull UUID id) {
        jpaUserBlacklistRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUserIdAndBlockedUserId(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        try {
            jpaUserBlacklistRepository.deleteByUserIdAndBlockedUserId(userId, blockedUserId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    "UserBlacklist[userId=%s, blockedUserId=%s] not found for deleting"
                            .formatted(userId, blockedUserId)
            );
        }
    }

    public List<UserBlacklist> findAllByUserId(@NonNull UUID userId) {
        return jpaUserBlacklistRepository.findAllByUserId(userId);
    }

    public Optional<UserBlacklist> findByUserIdAndBlockedUserId(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        return jpaUserBlacklistRepository.findByUserIdAndBlockedUserId(userId, blockedUserId);
    }

    public boolean existsByUserIdAndBlockedUserId(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        return jpaUserBlacklistRepository.existsByUserIdAndBlockedUserId(userId, blockedUserId);
    }

    public boolean existsAnyBetweenUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        return jpaUserBlacklistRepository.existsAnyBetweenUserIds(userId1, userId2);
    }

    public boolean existsAllBetweenUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        return jpaUserBlacklistRepository.existsAllBetweenUserIds(userId1, userId2);
    }

    public UserBlacklist getByUserIdAndBlockedUserId(@NonNull UUID userId, @NonNull UUID blockedUserId) {
        return jpaUserBlacklistRepository.findByUserIdAndBlockedUserId(userId, blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserBlacklist[userId=%s, blockedUserId=%s] not found".formatted(userId, blockedUserId)
                ));
    }

    public void validateAnyBetweenUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        if (jpaUserBlacklistRepository.existsAnyBetweenUserIds(userId1, userId2)) {
            throw new ResourceConflictException("This user has blocked you or you have blocked this user");
        }
    }

}
