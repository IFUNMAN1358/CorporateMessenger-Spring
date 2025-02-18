package com.nagornov.CorporateMessenger.infrastructure.security.filter;

import com.nagornov.CorporateMessenger.sharedKernel.logs.service.LogContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomTraceFilter extends GenericFilterBean {

    private static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest httpRequest) {

            String traceId = httpRequest.getHeader(TRACE_HEADER);

            if (!StringUtils.hasText(traceId)) {
                traceId = UUID.randomUUID().toString();
            }
            LogContextHolder.setTraceId(traceId);

            LogContextHolder.setSpanId(UUID.randomUUID().toString());
            LogContextHolder.setHttpMethod(httpRequest.getMethod());
            LogContextHolder.setHttpPath(httpRequest.getRequestURI());
        }
        try {
            chain.doFilter(request, response);
        } finally {
            LogContextHolder.clear();
        }
    }

}
