package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaContactRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaContactDomainService {

    private final JpaContactRepository jpaContactRepository;

    public void save(@NonNull Contact contact) {
        jpaContactRepository.save(contact);
    }

    public void delete(@NonNull Contact contact) {
        jpaContactRepository.delete(contact);
    }

    public void deleteById(@NonNull UUID id) {
        jpaContactRepository.deleteById(id);
    }

}
