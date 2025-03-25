package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRegistrationKeyEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaRegistrationKeyMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaRegistrationKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaRegistrationKeyRepository {

    private final SpringDataJpaRegistrationKeyRepository springDataJpaRegistrationKeyRepository;
    private final JpaRegistrationKeyMapper jpaRegistrationKeyMapper;

    public RegistrationKey save(RegistrationKey registrationKey) {
        JpaRegistrationKeyEntity entity = springDataJpaRegistrationKeyRepository.save(
                jpaRegistrationKeyMapper.toEntity(registrationKey)
        );
        return jpaRegistrationKeyMapper.toDomain(entity);
    }

    public void delete(RegistrationKey registrationKey) {
        springDataJpaRegistrationKeyRepository.delete(
                jpaRegistrationKeyMapper.toEntity(registrationKey)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaRegistrationKeyRepository.deleteById(id);
    }

    public Optional<RegistrationKey> findByValue(String key) {
        return springDataJpaRegistrationKeyRepository
                .findJpaRegistrationKeyEntityByValue(key)
                .map(jpaRegistrationKeyMapper::toDomain);
    }

}
