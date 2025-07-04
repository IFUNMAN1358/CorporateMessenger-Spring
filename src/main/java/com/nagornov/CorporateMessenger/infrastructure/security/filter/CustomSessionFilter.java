package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.domain.service.auth.ExternalServiceService;
import com.nagornov.CorporateMessenger.domain.service.auth.SessionService;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ExternalServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.SessionProperties;
import com.nagornov.CorporateMessenger.infrastructure.security.repository.JwtRepository;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomSessionFilter extends OncePerRequestFilter {

    private final JwtRepository jwtRepository;
    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
    private final ExternalServiceService externalServiceService;
    private final SessionService sessionService;


    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = JwtUtils.getTokenFromAuthorizationHeader(request.getHeader("Authorization"));

        if (accessToken != null && jwtRepository.validateAccessToken(accessToken)) {

            String sessionId = request.getHeader("X-Session-Id");

            if (sessionId == null || sessionId.isEmpty()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "X-Session-Id header is missing");
                return;
            }

            String serviceName = request.getHeader("X-Service-Name");

            if (serviceName == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "X-Service-Name header is missing");
                return;
            }

            Claims claims = jwtRepository.getAccessClaims(accessToken);
            JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);

            Optional<Session> existingSession = sessionService.findInRedis(
                    jwtInfoToken.getUserIdAsUUID(),
                    UUID.fromString(sessionId),
                    serviceName
            );

            if (existingSession.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session not found");
                return;
            }

            if (!isSafeMethod(request.getMethod())) {
                String serviceApiKey = request.getHeader("X-API-Key");

                if (
                        serviceApiKey == null
                        ||
                        !externalServiceService.getByNameAndApiKey(serviceName, serviceApiKey).isRequiresApiKey()
                ) {
                    CsrfToken cookieCsrfToken = cookieCsrfTokenRepository.loadToken(request);

                    if (cookieCsrfToken == null || !existingSession.get().getCsrfToken().equals(cookieCsrfToken.getToken())) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token missing or incorrect");
                        return;
                    }
                }
            }

            if (!existingSession.get().getAccessToken().equals(accessToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid jwt access token");
                return;
            }

            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSafeMethod(String method) {
        return "GET".equalsIgnoreCase(method) ||
               "HEAD".equalsIgnoreCase(method) ||
               "OPTIONS".equalsIgnoreCase(method) ||
               "TRACE".equalsIgnoreCase(method);
    }
}