package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtSession;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisJwtSessionDomainService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {

    private final JwtRepository jwtRepository;
    private final RedisJwtSessionDomainService redisJwtSessionDomainService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = JwtUtils.getTokenFromRequest(request);

        if (accessToken != null && jwtRepository.validateAccessToken(accessToken)) {
            Claims claims = jwtRepository.getAccessClaims(accessToken);
            JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);

            Optional<JwtSession> existingSession = redisJwtSessionDomainService.findByKey(
                UUID.fromString(jwtInfoToken.getUserId())
            );

            if (existingSession.isPresent()) {

                if (existingSession.get().getAccessToken().equals(accessToken)) {

                    jwtInfoToken.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);

                }
            }
        }
        filterChain.doFilter(request, response);
    }

}