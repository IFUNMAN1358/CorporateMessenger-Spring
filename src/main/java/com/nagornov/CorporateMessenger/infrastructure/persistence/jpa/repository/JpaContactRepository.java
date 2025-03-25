package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaContactEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaContactMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaContactRepository {

    private final SpringDataJpaContactRepository springDataJpaContactRepository;
    private final JpaContactMapper jpaContactMapper;

    public Contact save(Contact contact) {
        JpaContactEntity entity = springDataJpaContactRepository.save(
                jpaContactMapper.toEntity(contact)
        );
        return jpaContactMapper.toDomain(entity);
    }

    public void delete(Contact contact) {
        springDataJpaContactRepository.delete(
                jpaContactMapper.toEntity(contact)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaContactRepository.deleteById(id);
    }

}
