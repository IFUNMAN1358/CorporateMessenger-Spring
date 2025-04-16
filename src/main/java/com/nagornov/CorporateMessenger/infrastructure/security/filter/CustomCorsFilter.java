package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ServiceProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomCorsFilter extends OncePerRequestFilter {

    private final ServiceProperties serviceProperties;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", serviceProperties.getClientUrl());
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,PATCH,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Trace-Id");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        filterChain.doFilter(request, response);
    }

}