package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
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

@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {

    private final JwtRepository jwtRepository;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain fc
    ) throws ServletException, IOException {

        String accessToken = JwtUtils.getTokenFromRequest(request);

        if (accessToken != null && jwtRepository.validateAccessToken(accessToken)) {
            final Claims claims = jwtRepository.getAccessClaims(accessToken);
            final JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);
            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
        }
        fc.doFilter(request, response);
    }

}