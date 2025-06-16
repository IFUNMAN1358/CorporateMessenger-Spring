package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.SessionResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceForbiddenException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.auth.*;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ExternalServiceProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final ExternalServiceProperties externalServiceProperties;
    private final ExternalServiceService externalServiceService;
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final RoleService roleService;
    private final RegistrationKeyService registrationKeyService;
    private final EmployeeService employeeService;
    private final SessionService sessionService;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final CsrfService csrfService;


    @Transactional
    public SessionResponse registration(
            @NonNull HttpServletRequest servletReq,
            @NonNull HttpServletResponse servletRes,
            @NonNull RegistrationRequest request
    ) {
        verifyClientServiceHeader(servletReq);

        passwordService.ensureMatchConfirmPassword(request.getPassword(), request.getConfirmPassword());

        RegistrationKey registrationKey = registrationKeyService.getByValue(request.getRegistrationKey());
        registrationKey.ensureNotApplied();

        userService.ensureNotExistsByUsername(request.getUsername());

        String encodedPassword = passwordService.encodePassword(request.getPassword());
        User user = userService.create(
                request.getUsername(),
                encodedPassword,
                request.getFirstName(),
                request.getLastName()
        );

        registrationKeyService.apply(registrationKey, user.getId());

        Role role = roleService.assignRoleToUserId(user.getId(), RoleName.ROLE_USER);

        userSettingsService.create(user.getId());
        employeeService.create(user.getId());

        UUID sessionId = UUID.randomUUID();
        String accessToken = jwtService.generateAccessToken(user, List.of(role));
        String refreshToken = jwtService.generateRefreshToken(user);
        CsrfToken csrfToken = csrfService.generateToken(servletReq);

        sessionService.saveToRedis(
                user.getId(),
                sessionId,
                servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName()),
                accessToken,
                refreshToken,
                csrfToken.getToken()
        );

        csrfService.saveToken(csrfToken, servletReq, servletRes); // save csrf token in cookie
        return new SessionResponse(sessionId, accessToken, refreshToken); // return session response
    }


    @Transactional
    public SessionResponse login(
            @NonNull HttpServletRequest servletReq,
            @NonNull HttpServletResponse servletRes,
            @NonNull LoginRequest request
    ) {
        verifyClientServiceHeader(servletReq);

        User user = userService.getByUsername(request.getUsername());
        userService.validateUserNotDeleted(user);

        passwordService.ensureMatchEncodedPassword(request.getPassword(), user.getPassword());

        List<Role> roles = roleService.findAllByUserId(user.getId());

        UUID sessionId = UUID.randomUUID();
        String accessToken = jwtService.generateAccessToken(user, roles);
        String refreshToken = jwtService.generateRefreshToken(user);
        CsrfToken csrfToken = csrfService.generateToken(servletReq);

        sessionService.saveToRedis(
                user.getId(),
                sessionId,
                servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName()),
                accessToken,
                refreshToken,
                csrfToken.getToken()
        );

        csrfService.saveToken(csrfToken, servletReq, servletRes); // save csrf token in cookie
        return new SessionResponse(sessionId, accessToken, refreshToken); // return session response
    }


    @Transactional
    public void logout(
            @NonNull HttpServletRequest servletReq,
            @NonNull HttpServletResponse servletRes
    ) {
        verifyClientServiceHeader(servletReq);

        String sessionId = servletReq.getHeader("X-Session-Id");

        if (sessionId == null || sessionId.isEmpty()) {
            throw new ResourceForbiddenException("X-Session-Id header is missing");
        }

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        sessionService.deleteFromRedis(
                user.getId(),
                UUID.fromString(sessionId),
                servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName())
        );
    }


    private void verifyClientServiceHeader(@NonNull HttpServletRequest servletReq) {
        if (servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName()) == null) {
            throw new ResourceBadRequestException(
                    "%s header is null".formatted(externalServiceProperties.getHeaderName().getServiceName())
            );
        }

        if (
                externalServiceService.getByName(
                        servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName())
                ).isRequiresApiKey()
        ) {
            throw new ResourceNotFoundException("Header value is not valid for normal authorization");
        }
    }
}
