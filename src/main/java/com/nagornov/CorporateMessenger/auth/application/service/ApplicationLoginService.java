package com.nagornov.CorporateMessenger.auth.application.service;

import com.nagornov.CorporateMessenger.auth.application.dto.request.LoginFormRequest;
import com.nagornov.CorporateMessenger.auth.application.dto.response.JwtResponse;
import com.nagornov.CorporateMessenger.auth.application.mapper.AuthDtoUserMapper;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.domain.service.AuthJpaSessionService;
import com.nagornov.CorporateMessenger.auth.domain.service.AuthJpaUserService;
import com.nagornov.CorporateMessenger.auth.domain.service.AuthPasswordService;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.domain.service.AuthRedisSessionService;
import com.nagornov.CorporateMessenger.sharedKernel.security.interfaces.JwtService;
import com.nagornov.CorporateMessenger.sharedKernel.security.mapper.SecurityUserMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ApplicationLoginService {

    // services
    private final AuthJpaUserService jpaUserService;
    private final AuthJpaSessionService jpaSessionService;
    private final AuthRedisSessionService redisSessionService;
    private final AuthPasswordService passwordService;
    // mappers
    private final AuthDtoUserMapper dtoUserMapper;
    // shared
    private final JwtService jwtService;
    private final SecurityUserMapper securityUserMapper;


    @Transactional
    public JwtResponse login(@NotNull final LoginFormRequest request) {

        final AuthUser userFromRequest = dtoUserMapper.toUser(request);
        final AuthUser postgresUser = jpaUserService.getUserByUsername(userFromRequest.getUsername());

        passwordService.matchUserPassword(request.getPassword(), postgresUser.getPassword());

        final String accessToken = jwtService.generateAccessToken(
                    securityUserMapper.toSecurityUser(postgresUser)
        );
        final String refreshToken = jwtService.generateRefreshToken(
                securityUserMapper.toSecurityUser(postgresUser)
        );

        final AuthSession session = jpaSessionService.createOrUpdateSession(postgresUser, accessToken, refreshToken);
        redisSessionService.createSession(session, 24, TimeUnit.HOURS);

        return new JwtResponse(accessToken, refreshToken);
    }

}
