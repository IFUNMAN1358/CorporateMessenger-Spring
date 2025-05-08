package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactRole;
import com.nagornov.CorporateMessenger.domain.enums.model.ContactStatus;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import com.nagornov.CorporateMessenger.domain.dto.ContactPairDTO;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaContactRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final JpaContactRepository jpaContactRepository;

    @Transactional
    public Contact create(
            @NonNull UUID userId,
            @NonNull UUID contactId,
            @NonNull ContactRole role,
            @NonNull ContactStatus status,
            Instant lastRequestSentAt,
            Instant addedAt
    ) {
        Contact contact = new Contact(
                UUID.randomUUID(),
                userId,
                contactId,
                role,
                status,
                lastRequestSentAt,
                addedAt
        );
        return jpaContactRepository.save(contact);
    }

    @Transactional
    public Contact update(@NonNull Contact contact) {
        return jpaContactRepository.save(contact);
    }

    @Transactional
    public List<Contact> updateAll(@NonNull List<Contact> contacts) {
        return jpaContactRepository.saveAll(contacts);
    }

    @Transactional
    public void delete(@NonNull Contact contact) {
        jpaContactRepository.delete(contact);
    }

    @Transactional
    public void deleteAll(@NonNull List<Contact> contacts) {
        jpaContactRepository.deleteAll(contacts);
    }

    @Transactional
    public void deleteContactPairByUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        try {
            jpaContactRepository.deleteContactPairByUserIds(userId1, userId2);
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    "Contact[userId=%s, contactId=%s] or Contact[userId=%s, contactId=%s] not found for deleting"
                            .formatted(userId1, userId2, userId2, userId1)
            );
        }
    }

    public List<Contact> findAllByUserId(@NonNull UUID userId) {
        return jpaContactRepository.findAllByUserId(userId);
    }

    public Page<Contact> findAllByUserId(@NonNull UUID userId, int page, int size) {
        return jpaContactRepository.findAllByUserId(userId, page, size);
    }

    public Optional<Contact> findByUserIdAndContactId(@NonNull UUID userId, @NonNull UUID contactId) {
        return jpaContactRepository.findByUserIdAndContactId(userId, contactId);
    }

    public Optional<ContactPairDTO> findContactPairByUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        return jpaContactRepository.findContactPairByUserIds(userId1, userId2);
    }

    public boolean existsContactPairByUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        return jpaContactRepository.existsContactPairByUserIds(userId1, userId2);
    }

    public Contact getByUserIdAndContactId(@NonNull UUID userId, @NonNull UUID contactId) {
        return jpaContactRepository.findByUserIdAndContactId(userId, contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact[userId=%s, contactId=%s] not found".formatted(userId, contactId)));
    }

    public ContactPairDTO getContactPairByUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        return jpaContactRepository.findContactPairByUserIds(userId1, userId2)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContactPairDTO[Contact, Contact] by Contact[userId=%s, contactId=%s] or Contact[userId=%s, contactId=%s] not found"
                                .formatted(userId1, userId2, userId2, userId1)
                ));
    }

}
