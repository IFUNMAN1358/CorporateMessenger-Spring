package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.service.auth.CsrfService;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.HttpMethodUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomCsrfFilter extends OncePerRequestFilter {

    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
    private final CsrfService csrfService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (HttpMethodUtils.isSafeMethod(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        CsrfToken csrfToken = cookieCsrfTokenRepository.loadToken(request);

        if (csrfToken.getToken() == null || !csrfService.existsInRedis(csrfToken.getToken())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token missing or incorrect");
            return;
        }
        filterChain.doFilter(request, response);
    }

}