package com.nagornov.CorporateMessenger.auth.application.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.domain.service.AuthJpaSessionService;
import com.nagornov.CorporateMessenger.auth.domain.service.AuthJpaUserService;
import com.nagornov.CorporateMessenger.auth.domain.service.AuthRedisSessionService;
import com.nagornov.CorporateMessenger.sharedKernel.security.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.sharedKernel.security.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationLogoutService {

    // services
    private final AuthJpaUserService jpaUserService;
    private final AuthJpaSessionService jpaSessionService;
    private final AuthRedisSessionService redisSessionService;
    // shared
    private final JwtService jwtService;


    @Transactional
    public void logout() {
        final JwtAuthentication authInfo = jwtService.getAuthInfo();

        final AuthUser postgresUser = jpaUserService.getUserById(
                UUID.fromString(authInfo.getUserId())
        );

        jpaUserService.removeSession(postgresUser);
        jpaSessionService.deleteSession(postgresUser.getSession());
        redisSessionService.deleteSession(postgresUser.getSession());
    }

}
