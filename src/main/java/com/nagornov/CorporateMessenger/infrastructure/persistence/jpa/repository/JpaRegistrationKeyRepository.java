package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRegistrationKeyEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaRegistrationKeyMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaRegistrationKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public Optional<RegistrationKey> findById(UUID id) {
        return springDataJpaRegistrationKeyRepository
                .findById(id)
                .map(jpaRegistrationKeyMapper::toDomain);
    }

    public Optional<RegistrationKey> findByValue(String value) {
        return springDataJpaRegistrationKeyRepository
                .findByValue(value)
                .map(jpaRegistrationKeyMapper::toDomain);
    }

    public List<RegistrationKey> findAllSortedByNotApplied(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return springDataJpaRegistrationKeyRepository.findAllSortedByNotApplied(pageable)
                .map(jpaRegistrationKeyMapper::toDomain).toList();
    }

}
