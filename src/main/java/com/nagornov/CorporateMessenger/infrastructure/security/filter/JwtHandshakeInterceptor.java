package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.domain.service.auth.SessionService;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ExternalServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.security.repository.JwtRepository;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final ExternalServiceProperties externalServiceProperties;
    private final JwtRepository jwtRepository;
    private final SessionService sessionService;

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        // Required for all requests
        String serviceName = servletRequest.getHeader(externalServiceProperties.getHeaderName().getServiceName());

        if (serviceName == null) {
            return false;
        }

        // Required for authorized requests
        String accessToken = JwtUtils.getTokenFromAuthorizationHeader(servletRequest.getHeader("Authorization"));

        if (accessToken != null && jwtRepository.validateAccessToken(accessToken)) {
            Claims claims = jwtRepository.getAccessClaims(accessToken);
            JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);

            Optional<Session> existingSession = sessionService.findInRedis(
                    jwtInfoToken.getUserIdAsUUID(),
                    serviceName
            );

            if (existingSession.isPresent()) {

                if (existingSession.get().getAccessToken().equals(accessToken)) {

                    jwtInfoToken.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
                    attributes.put("userId", jwtInfoToken.getUserIdAsUUID());
                    return true;

                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception
    ) {

    }

}
