package com.nagornov.CorporateMessenger.domain.service.businessService.jpa;

import com.nagornov.CorporateMessenger.domain.model.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaRegistrationKeyDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaRegistrationKeyBusinessService {

    private final JpaRegistrationKeyDomainService jpaRegistrationKeyDomainService;

    public void apply(@NotNull RegistrationKey registrationKey) {
        RegistrationKey savingKey = new RegistrationKey(
                registrationKey.getId(),
                registrationKey.getValue(),
                true,
                registrationKey.getCreatedAt()
        );
        jpaRegistrationKeyDomainService.update(savingKey);
    }

}
