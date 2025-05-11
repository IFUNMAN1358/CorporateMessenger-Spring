package com.nagornov.CorporateMessenger.domain.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CsrfService {

    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;

    public CsrfToken loadToken(@NonNull HttpServletRequest request) {
        return cookieCsrfTokenRepository.loadToken(request);
    }

    public CsrfToken generateToken(@NonNull HttpServletRequest request) {
        return cookieCsrfTokenRepository.generateToken(request);
    }

    public void saveToken(
            @NonNull CsrfToken csrfToken,
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response
    ) {
        cookieCsrfTokenRepository.saveToken(csrfToken, request, response);
    }

}
