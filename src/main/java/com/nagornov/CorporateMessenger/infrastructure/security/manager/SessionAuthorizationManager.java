package com.nagornov.CorporateMessenger.infrastructure.security.manager;

import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisSessionRepository;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class SessionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final RedisSessionRepository redisSessionRepository;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();

        String accessToken = JwtUtils.getTokenFromRequest(request);
        if (accessToken == null) {
            return new AuthorizationDecision(false);
        }

        final JwtAuthentication authInfo = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();

        final Optional<Session> existingSession = redisSessionRepository.findById(
                UUID.fromString(authInfo.getUserId())
        );
        if (existingSession.isEmpty()) {
            return new AuthorizationDecision(false);
        }
        if (!existingSession.get().getAccessToken().equals(accessToken)) {
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
