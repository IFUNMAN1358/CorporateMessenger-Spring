package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationKeyApplicationService {


    @Transactional
    public RegistrationKey createRegistrationKey() {
        return null;
    }


    @Transactional(readOnly = true)
    public List<RegistrationKey> findAllRegistrationKeys(int page, int size) {
        return List.of();
    }


    @Transactional
    public void deleteRegistrationKeyById(@NonNull UUID keyId) {

    }
}