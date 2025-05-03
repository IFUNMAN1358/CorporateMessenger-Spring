package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.UserBlacklist;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserBlacklistMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserBlacklistRepository {

    private final SpringDataJpaUserBlacklistRepository springDataJpaUserBlacklistRepository;
    private final JpaUserBlacklistMapper jpaUserBlacklistMapper;

    public UserBlacklist save(UserBlacklist userBlacklist) {
        return jpaUserBlacklistMapper.toDomain(
                springDataJpaUserBlacklistRepository.save(
                        jpaUserBlacklistMapper.toEntity(userBlacklist)
                )
        );
    }

    public void delete(UserBlacklist userBlacklist) {
        springDataJpaUserBlacklistRepository.delete(
                jpaUserBlacklistMapper.toEntity(userBlacklist)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaUserBlacklistRepository.deleteById(id);
    }

    public void deleteByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        springDataJpaUserBlacklistRepository.deleteByUserIdAndBlockedUserId(userId, blockedUserId);
    }

    public List<UserBlacklist> findAllByUserId(UUID userId) {
        return springDataJpaUserBlacklistRepository.findAllByUserId(userId)
                .stream().map(jpaUserBlacklistMapper::toDomain).toList();
    }

    public Optional<UserBlacklist> findByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        return springDataJpaUserBlacklistRepository.findByUserIdAndBlockedUserId(userId, blockedUserId)
                .map(jpaUserBlacklistMapper::toDomain);
    }

    public boolean existsByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId) {
        return springDataJpaUserBlacklistRepository.existsByUserIdAndBlockedUserId(userId, blockedUserId);
    }

    public boolean existsAnyBetweenUserIds(UUID userId1, UUID userId2) {
        return springDataJpaUserBlacklistRepository.existsAnyBetweenUserIds(userId1, userId2);
    }

    public boolean existsAllBetweenUserIds(UUID userId1, UUID userId2) {
        return springDataJpaUserBlacklistRepository.existsAllBetweenUserIds(userId1, userId2);
    }

}
