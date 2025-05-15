package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationKeyResponse;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.user.RegistrationKeyService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationKeyApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final RegistrationKeyService registrationKeyService;


    @Transactional
    public RegistrationKeyResponse create() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        RegistrationKey rk = registrationKeyService.create();
        return new RegistrationKeyResponse(rk);
    }


    @Transactional(readOnly = true)
    public List<RegistrationKeyResponse> findAll(int page, int size) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        return registrationKeyService.findAllSortedByNotApplied(page, size)
                .stream().map(RegistrationKeyResponse::new).toList();
    }


    @Transactional
    public void deleteByKeyId(@NonNull UUID keyId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        RegistrationKey rk = registrationKeyService.getById(keyId);
        registrationKeyService.delete(rk);
    }
}