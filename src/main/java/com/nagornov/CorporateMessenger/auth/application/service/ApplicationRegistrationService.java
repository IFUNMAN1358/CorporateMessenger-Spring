package com.nagornov.CorporateMessenger.auth.application.service;

import com.nagornov.CorporateMessenger.auth.application.dto.request.RegistrationFormRequest;
import com.nagornov.CorporateMessenger.auth.application.dto.response.JwtResponse;
import com.nagornov.CorporateMessenger.auth.application.mapper.AuthDtoUserMapper;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthRole;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.domain.service.*;
import com.nagornov.CorporateMessenger.auth.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.sharedKernel.security.interfaces.JwtService;
import com.nagornov.CorporateMessenger.sharedKernel.security.mapper.SecurityUserMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ApplicationRegistrationService {

    // services
    private final AuthJpaUserService jpaUserService;
    private final AuthJpaRoleService jpaRoleService;
    private final AuthJpaSessionService jpaSessionService;
    private final AuthJpaRegistrationKeyService jpaRegistrationKeyService;
    private final AuthCassandraUserService cassandraUserService;
    private final AuthRedisSessionService redisSessionService;
    private final AuthPasswordService passwordService;
    // mappers
    private final AuthDtoUserMapper dtoUserMapper;
    // shared
    private final JwtService jwtService;
    private final SecurityUserMapper securityUserMapper;


    @Transactional
    public JwtResponse registration(@NotNull final RegistrationFormRequest request) {

        jpaRegistrationKeyService.validateAndApplyRegistrationKey(request.getRegistrationKey());

        final AuthUser userFromRequest = dtoUserMapper.toUser(request);
        final AuthUser initializedUser = AuthUser.initializeUser(userFromRequest);
        final AuthUser userWithEncodedPassword = passwordService.encodeUserPassword(initializedUser);

        try {
            final AuthUser postgresUser = jpaUserService.createUser(userWithEncodedPassword);
            cassandraUserService.createUser(postgresUser);

            final AuthRole userRole = jpaRoleService.getRoleByName(RoleEnum.USER);
            jpaUserService.addRole(postgresUser, userRole);

            final String accessToken = jwtService.generateAccessToken(
                    securityUserMapper.toSecurityUser(postgresUser)
            );
            final String refreshToken = jwtService.generateRefreshToken(
                    securityUserMapper.toSecurityUser(postgresUser)
            );

            final AuthSession session = jpaSessionService.createSession(postgresUser, accessToken, refreshToken);
            redisSessionService.createSession(session, 24, TimeUnit.HOURS);

            return new JwtResponse(accessToken, refreshToken);
        } catch (Exception e) {
            jpaUserService.deleteUser(initializedUser);
            cassandraUserService.deleteUser(initializedUser);
            throw e;
        }
    }

}
