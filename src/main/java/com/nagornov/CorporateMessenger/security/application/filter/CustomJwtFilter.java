package com.nagornov.CorporateMessenger.security.application.filter;

import com.nagornov.CorporateMessenger.sharedKernel.security.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.security.infrastructure.repository.JwtRepository;
import com.nagornov.CorporateMessenger.security.infrastructure.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {

    private final JwtRepository jwtRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws ServletException, IOException {

        String token = JwtUtils.getTokenFromRequest(request);

        if (token != null && jwtRepository.validateAccessToken(token)) {
            final Claims claims = jwtRepository.getAccessClaims(token);
            final JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);
            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
        }
        fc.doFilter(request, response);
    }

}