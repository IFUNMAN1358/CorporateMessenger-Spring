package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.mapper.DtoAuthMapper;
import com.nagornov.CorporateMessenger.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.domain.factory.SessionFactory;
import com.nagornov.CorporateMessenger.domain.logger.ApplicationServiceLogger;
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
    private final ApplicationServiceLogger applicationServiceLogger;


    @Transactional
    public JwtResponse registration(@NotNull RegistrationRequest request) {
        try {
            applicationServiceLogger.info("Registration started");

            jpaUserDomainService.validateUserDoesNotExistByUsername(request.getUsername());

            RegistrationKey registrationKey = jpaRegistrationKeyDomainService.getByValue(request.getRegistrationKey());
            registrationKey.validateApplied();
            jpaRegistrationKeyBusinessService.apply(registrationKey);

            User requestUser = dtoAuthMapper.fromRegistrationRequest(request);
            requestUser.setRandomId();

            String encodedPassword = passwordDomainService.encodePassword(request.getPassword());
            requestUser.updatePassword(encodedPassword);

            Session sessionWithId = SessionFactory.createWithRandomId();

            Role userRole = jpaRoleDomainService.getByName(RoleEnum.USER);
            requestUser.addRole(userRole);

            jpaUserDomainService.save(requestUser);

            String accessToken = jwtDomainService.generateAccessToken(requestUser);
            String refreshToken = jwtDomainService.generateRefreshToken(requestUser);

            sessionWithId.updateAccessToken(accessToken);
            sessionWithId.updateRefreshToken(refreshToken);

            redisSessionDomainService.save(requestUser.getId(), sessionWithId);

            applicationServiceLogger.info("Registration finished");

            return new JwtResponse(accessToken, refreshToken);

        } catch (Exception e) {
            applicationServiceLogger.error("Registration failed", e);
            throw e;
        }
    }


    @Transactional
    public JwtResponse login(@NotNull LoginRequest request) {
        try {
            applicationServiceLogger.info("Login started");

            User userFromRequest = dtoAuthMapper.fromLoginRequest(request);
            User postgresUser = jpaUserDomainService.getByUsername(userFromRequest.getUsername());

            passwordDomainService.matchPassword(request.getPassword(), postgresUser.getPassword());

            Session sessionWithId = SessionFactory.createWithRandomId();

            String accessToken = jwtDomainService.generateAccessToken(postgresUser);
            String refreshToken = jwtDomainService.generateRefreshToken(postgresUser);

            sessionWithId.updateAccessToken(accessToken);
            sessionWithId.updateRefreshToken(refreshToken);

            redisSessionDomainService.save(postgresUser.getId(), sessionWithId);

            applicationServiceLogger.info("Login finished");

            return new JwtResponse(accessToken, refreshToken);

        } catch (Exception e) {
            applicationServiceLogger.error("Login failed", e);
            throw e;
        }

    }


    @Transactional
    public HttpResponse logout() {
        try {
            applicationServiceLogger.info("Logout started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    UUID.fromString(authInfo.getUserId())
            );

            redisSessionDomainService.deleteByUserId(postgresUser.getId());

            applicationServiceLogger.info("Logout finished");

            return new HttpResponse("Successfully logged out", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Logout failed", e);
            throw e;
        }
    }
}
