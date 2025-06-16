package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.ExternalAuthResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.auth.*;
import com.nagornov.CorporateMessenger.domain.service.user.RoleService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ExternalServiceProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExternalAuthApplicationService {

    private final SessionService sessionService;
    private final ExternalServiceProperties externalServiceProperties;
    private final ExternalServiceService externalServiceService;
    private final UserService userService;
    private final PasswordService passwordService;
    private final RoleService roleService;
    private final JwtService jwtService;


    @Transactional(readOnly = true)
    public ExternalAuthResponse externalLogin(
            @NonNull HttpServletRequest servletReq,
            @NonNull HttpServletResponse servletRes,
            @NonNull LoginRequest request
    ) {
        verifyExternalServiceHeaders(servletReq);

        User user = userService.getByUsername(request.getUsername());
        userService.validateUserNotDeleted(user);

        passwordService.ensureMatchEncodedPassword(request.getPassword(), user.getPassword());

        List<Role> roles = roleService.findAllByUserId(user.getId());

        UUID sessionId = UUID.randomUUID();
        String accessToken = jwtService.generateAccessToken(user, roles);
        String refreshToken = jwtService.generateRefreshToken(user);

        sessionService.saveToRedis(
                user.getId(),
                sessionId,
                servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName()),
                accessToken,
                refreshToken,
                null
        );

        return new ExternalAuthResponse(sessionId, accessToken, refreshToken, user, roles);
    }


    private void verifyExternalServiceHeaders(@NonNull HttpServletRequest servletReq) {
        if (
             servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName()) == null
             ||
             servletReq.getHeader(externalServiceProperties.getHeaderName().getApiKey()) == null
        ) {
            throw new ResourceBadRequestException(
                    "%s or %s header is null"
                            .formatted(
                                    externalServiceProperties.getHeaderName().getServiceName(),
                                    externalServiceProperties.getHeaderName().getApiKey()
                            )
            );
        }

        if (
                !externalServiceService.getByNameAndApiKey(
                        servletReq.getHeader(externalServiceProperties.getHeaderName().getServiceName()),
                        servletReq.getHeader(externalServiceProperties.getHeaderName().getApiKey())
                ).isRequiresApiKey()
        ) {
            throw new ResourceNotFoundException(
                    "Service not found by service name from header or cannot be authorized via external login because it does not require an API key"
            );
        }
    }
}