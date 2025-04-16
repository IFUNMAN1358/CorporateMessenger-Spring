package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomTraceFilter extends OncePerRequestFilter {

    private static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String traceId = request.getHeader(TRACE_HEADER);

        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put("traceId", traceId);

        String userId = null;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication != null && authentication.isAuthenticated() &&
                        authentication instanceof JwtAuthentication authInfo
        ) {
            userId = authInfo.getUserId();
        }
        MDC.put("userId", userId);

        MDC.put("spanId", UUID.randomUUID().toString());
        MDC.put("httpMethod", request.getMethod());
        MDC.put("httpPath", request.getRequestURI());

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
