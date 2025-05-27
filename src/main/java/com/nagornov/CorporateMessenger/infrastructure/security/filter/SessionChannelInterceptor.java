package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.domain.service.auth.SessionService;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ExternalServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.security.repository.JwtRepository;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionChannelInterceptor implements ChannelInterceptor {

    private final ExternalServiceProperties externalServiceProperties;
    private final JwtRepository jwtRepository;
    private final SessionService sessionService;

    @Override
    public Message<?> preSend(
            @NotNull Message<?> message,
            @NotNull MessageChannel channel
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authHeader = accessor.getNativeHeader("Authorization");
            List<String> serviceNameHeader = accessor.getNativeHeader(
                    externalServiceProperties.getHeaderName().getServiceName()
            );

            String accessToken = authHeader != null && !authHeader.isEmpty()
                ? JwtUtils.getTokenFromAuthorizationHeader(authHeader.getFirst())
                : null;
            String serviceName = serviceNameHeader != null && !serviceNameHeader.isEmpty()
                ? serviceNameHeader.getFirst()
                : null;

            if (accessToken == null || !jwtRepository.validateAccessToken(accessToken)) {
                throw new SecurityException("Invalid or missing token");
            }

            if (serviceName == null) {
                throw new SecurityException("Missing X-Service-Name");
            }

            Claims claims = jwtRepository.getAccessClaims(accessToken);
            JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);
            Optional<Session> existingSession = sessionService.findInRedis(
                jwtInfoToken.getUserIdAsUUID(), serviceName
            );

            if (existingSession.isPresent() && existingSession.get().getAccessToken().equals(accessToken)) {
                jwtInfoToken.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
                accessor.setUser(jwtInfoToken);
            } else {
                throw new SecurityException("Invalid session or token mismatch");
            }
        }
        return message;
    }
}