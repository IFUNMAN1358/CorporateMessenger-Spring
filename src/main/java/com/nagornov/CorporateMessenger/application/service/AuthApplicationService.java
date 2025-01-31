package com.nagornov.CorporateMessenger.application.service;

import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.common.InformationalResponse;
import com.nagornov.CorporateMessenger.application.mapper.DtoAuthMapper;
import com.nagornov.CorporateMessenger.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.domain.factory.SessionFactory;
import com.nagornov.CorporateMessenger.domain.model.*;
import com.nagornov.CorporateMessenger.domain.service.businessService.jpa.JpaRegistrationKeyBusinessService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaRegistrationKeyDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaRoleDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisSessionDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.PasswordDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JpaUserDomainService jpaUserDomainService;
    private final JpaRoleDomainService jpaRoleDomainService;
    private final JpaRegistrationKeyDomainService jpaRegistrationKeyDomainService;
    private final JpaRegistrationKeyBusinessService jpaRegistrationKeyBusinessService;
    private final RedisSessionDomainService redisSessionDomainService;
    private final PasswordDomainService passwordDomainService;
    private final DtoAuthMapper dtoAuthMapper;
    private final JwtDomainService jwtDomainService;


    @Transactional
    public JwtResponse registration(@NotNull RegistrationRequest request) {

        jpaUserDomainService.validateUserDoesNotExistByUsername(request.getUsername());

        final RegistrationKey registrationKey = jpaRegistrationKeyDomainService.getByValue(request.getRegistrationKey());
        registrationKey.validateApplied();
        jpaRegistrationKeyBusinessService.apply(registrationKey);

        final User requestUser = dtoAuthMapper.fromRegistrationRequest(request);
        requestUser.setRandomId();

        final String encodedPassword = passwordDomainService.encodePassword(request.getPassword());
        requestUser.updatePassword(encodedPassword);

        final Session sessionWithId = SessionFactory.createWithRandomId();

        final Role userRole = jpaRoleDomainService.getByName(RoleEnum.USER);
        requestUser.addRole(userRole);

        jpaUserDomainService.save(requestUser);

        final String accessToken = jwtDomainService.generateAccessToken(requestUser);
        final String refreshToken = jwtDomainService.generateRefreshToken(requestUser);

        sessionWithId.updateAccessToken(accessToken);
        sessionWithId.updateRefreshToken(refreshToken);

        redisSessionDomainService.save(requestUser.getId(), sessionWithId);

        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public JwtResponse login(@NotNull LoginRequest request) {

        final User userFromRequest = dtoAuthMapper.fromLoginRequest(request);
        final User postgresUser = jpaUserDomainService.getByUsername(userFromRequest.getUsername());

        passwordDomainService.matchPassword(request.getPassword(), postgresUser.getPassword());

        final Session sessionWithId = SessionFactory.createWithRandomId();

        final String accessToken = jwtDomainService.generateAccessToken(postgresUser);
        final String refreshToken = jwtDomainService.generateRefreshToken(postgresUser);

        sessionWithId.updateAccessToken(accessToken);
        sessionWithId.updateRefreshToken(refreshToken);

        redisSessionDomainService.save(postgresUser.getId(), sessionWithId);

        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public InformationalResponse logout() {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );
        redisSessionDomainService.deleteByUserId(postgresUser.getId());

        return new InformationalResponse("Successfully logged out");
    }
}
