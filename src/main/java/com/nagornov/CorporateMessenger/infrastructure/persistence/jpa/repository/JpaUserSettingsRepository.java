package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserSettingsMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserSettingsRepository {

    private final SpringDataJpaUserSettingsRepository springDataJpaUserSettingsRepository;
    private final JpaUserSettingsMapper jpaUserSettingsMapper;

    public UserSettings save(UserSettings userSettings) {
        return jpaUserSettingsMapper.toDomain(
                springDataJpaUserSettingsRepository.save(
                        jpaUserSettingsMapper.toEntity(userSettings)
                )
        );
    }

    public Optional<UserSettings> findByUserId(UUID userId) {
        return springDataJpaUserSettingsRepository.findByUserId(userId).map(jpaUserSettingsMapper::toDomain);
    }

}
