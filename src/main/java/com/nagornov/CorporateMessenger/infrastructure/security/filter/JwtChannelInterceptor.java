package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.domain.service.auth.SessionService;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ExternalServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.security.repository.JwtRepository;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final ExternalServiceProperties externalServiceProperties;
    private final JwtRepository jwtRepository;
    private final SessionService sessionService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == null || !"CONNECT".equals(accessor.getCommand().toString())) {
            return message;
        }

        // Required for all requests
        String serviceName = (String) accessor.getHeader(externalServiceProperties.getHeaderName().getServiceName());

        if (serviceName == null) {
            throw new RuntimeException("%s header is missing".formatted(externalServiceProperties.getHeaderName().getServiceName()));
        }

        // Required for authorized requests
        String accessToken = JwtUtils.getTokenFromAuthorizationHeader((String) accessor.getHeader("Authorization"));

        if (accessToken != null && jwtRepository.validateAccessToken(accessToken)) {

            Claims claims = jwtRepository.getAccessClaims(accessToken);
            JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);

            Optional<Session> existingSession = sessionService.findInRedis(jwtInfoToken.getUserIdAsUUID(), serviceName);

            if (existingSession.isPresent()) {

                if (existingSession.get().getAccessToken().equals(accessToken)) {

                    jwtInfoToken.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
                    return message;

                }
            }

        }
        throw new RuntimeException("Invalid or expired token");
    }
}