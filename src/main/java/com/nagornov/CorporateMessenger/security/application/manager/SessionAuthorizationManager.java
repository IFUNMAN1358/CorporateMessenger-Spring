package com.nagornov.CorporateMessenger.security.application.manager;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.domain.service.*;
import com.nagornov.CorporateMessenger.security.infrastructure.util.JwtUtils;
import com.nagornov.CorporateMessenger.sharedKernel.security.model.JwtAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class SessionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final SecurityJwtService jwtService;
    private final SecurityRedisSessionService redisSessionService;
    private final SecurityRedisUserService redisUserService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();

        //String token = JwtUtils.getTokenFromRequest(request);

        final JwtAuthentication authInfo = jwtService.getAuthInfo();

        final Optional<SecurityUser> existingUser = redisUserService.findUserById(
                UUID.fromString(authInfo.getUserId())
        );
        if (existingUser.isEmpty()) {
            return new AuthorizationDecision(false);
        }

        final Optional<SecuritySession> existingSession = redisSessionService.findSessionById(
                existingUser.get().getSession().getId()
        );
        if (existingSession.isEmpty()) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(true);
    }

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationDecision decision = check(authentication, object);
        if (!decision.isGranted()) {
            throw new org.springframework.security.access.AccessDeniedException("Session does not exist");
        }
    }

}
