package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import com.nagornov.CorporateMessenger.domain.dto.ContactPairDTO;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaContactEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaContactMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaContactRepository {

    private final SpringDataJpaContactRepository springDataJpaContactRepository;
    private final JpaContactMapper jpaContactMapper;

    public Contact save(Contact contact) {
        return jpaContactMapper.toDomain(
                springDataJpaContactRepository.save(
                        jpaContactMapper.toEntity(contact)
                )
        );
    }

    public List<Contact> saveAll(List<Contact> contacts) {
        List<JpaContactEntity> jpaContactEntities = contacts.stream().map(jpaContactMapper::toEntity).toList();
        List<JpaContactEntity> savedContactEntities = springDataJpaContactRepository.saveAll(jpaContactEntities);
        return savedContactEntities.stream().map(jpaContactMapper::toDomain).toList();
    }

    public void delete(Contact contact) {
        springDataJpaContactRepository.delete(
                jpaContactMapper.toEntity(contact)
        );
    }

    public void deleteAll(List<Contact> contacts) {
        List<JpaContactEntity> jpaContactEntities = contacts.stream().map(jpaContactMapper::toEntity).toList();
        springDataJpaContactRepository.deleteAll(jpaContactEntities);
    }

    public void deleteContactPairByUserIds(UUID userId1, UUID userId2) {
        springDataJpaContactRepository.deleteContactPairByUserIds(userId1, userId2);
    }

    public Optional<Contact> findByUserIdAndContactId(UUID userId, UUID contactId) {
        return springDataJpaContactRepository.findByUserIdAndContactId(userId, contactId)
                .map(jpaContactMapper::toDomain);
    }

    public List<Contact> findAllByUserId(UUID userId) {
        return springDataJpaContactRepository.findAllByUserId(userId)
                .stream().map(jpaContactMapper::toDomain).toList();
    }

    public Page<Contact> findAllByUserId(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return springDataJpaContactRepository.findAllByUserId(userId, pageable).map(jpaContactMapper::toDomain);
    }

    public boolean existsContactPairByUserIds(UUID userId1, UUID userId2) {
        return springDataJpaContactRepository.existsContactPairByUserIds(userId1, userId2);
    }

    public Optional<ContactPairDTO> findContactPairByUserIds(UUID userId1, UUID userId2) {
        return springDataJpaContactRepository.findContactPairByUserIds(userId1, userId2)
                .map(dtoEntity -> {
                    return new ContactPairDTO(
                            jpaContactMapper.toDomain(dtoEntity.getContact1()),
                            jpaContactMapper.toDomain(dtoEntity.getContact2())
                    );
                });
    }

}
