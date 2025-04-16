package com.nagornov.CorporateMessenger.infrastructure.security.repository;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.CsrfProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CsrfRepository {

    private final CsrfProperties csrfProperties;

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();

        repository.setHeaderName(csrfProperties.getHeaderName());
        repository.setCookieName(csrfProperties.getCookie().getName());

        repository.setCookieCustomizer(cookieBuilder ->
            cookieBuilder
                .domain(csrfProperties.getCookie().getDomain())
                .path(csrfProperties.getCookie().getPath())
                .httpOnly(csrfProperties.getCookie().getHttpOnly())
                .secure(csrfProperties.getCookie().getSecure())
                .sameSite(csrfProperties.getCookie().getSameSite())
                .maxAge(csrfProperties.getCookie().getMaxAge())
        );
        return repository;
    }
}
