package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.ExternalServiceRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.ExternalServiceResponse;
import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.service.auth.ExternalServiceService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalServiceApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final ExternalServiceService externalServiceService;


    @Transactional
    public ExternalServiceResponse create(@NonNull ExternalServiceRequest request) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        ExternalService newExternalService = externalServiceService.create(request.getName(), request.getRequiresApiKey());

        return new ExternalServiceResponse(newExternalService);
    }


    @Transactional(readOnly = true)
    public ExternalServiceResponse getByServiceName(@NonNull String serviceName) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        ExternalService externalService = externalServiceService.getByName(serviceName);
        return new ExternalServiceResponse(externalService);
    }


    @Transactional(readOnly = true)
    public List<ExternalServiceResponse> findAll() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        return externalServiceService.findAll().stream().map(ExternalServiceResponse::new).toList();
    }


    @Transactional
    public ExternalServiceResponse generateNewApiKeyByServiceName(@NonNull String serviceName) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        ExternalService es = externalServiceService.getByName(serviceName);
        ExternalService updExternalService = externalServiceService.updateWithNewGeneratedApiKey(es);
        return new ExternalServiceResponse(updExternalService);
    }


    @Transactional
    public void deleteByServiceName(@NonNull String serviceName) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        userService.ensureExistsById(authInfo.getUserIdAsUUID());

        ExternalService es = externalServiceService.getByName(serviceName);
        externalServiceService.delete(es);
    }
}