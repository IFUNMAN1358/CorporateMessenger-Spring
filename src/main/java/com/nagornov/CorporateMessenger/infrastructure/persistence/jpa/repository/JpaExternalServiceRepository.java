package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaExternalServiceMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaExternalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaExternalServiceRepository {

    private final SpringDataJpaExternalServiceRepository springDataJpaExternalServiceRepository;
    private final JpaExternalServiceMapper jpaExternalServiceMapper;

    public ExternalService save(ExternalService externalService) {
        return jpaExternalServiceMapper.toDomain(
                springDataJpaExternalServiceRepository.save(
                        jpaExternalServiceMapper.toEntity(externalService)
                )
        );
    }

    public void delete(ExternalService externalService) {
        springDataJpaExternalServiceRepository.delete(
                jpaExternalServiceMapper.toEntity(externalService)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaExternalServiceRepository.deleteById(id);
    }

    public Optional<ExternalService> findById(UUID id) {
        return springDataJpaExternalServiceRepository.findById(id).map(jpaExternalServiceMapper::toDomain);
    }

    public Optional<ExternalService> findByName(String name) {
        return springDataJpaExternalServiceRepository.findByName(name).map(jpaExternalServiceMapper::toDomain);
    }

    public Optional<ExternalService> findByNameAndApiKey(String name, String apiKey) {
        return springDataJpaExternalServiceRepository.findByNameAndApiKey(name, apiKey).map(jpaExternalServiceMapper::toDomain);
    }

}