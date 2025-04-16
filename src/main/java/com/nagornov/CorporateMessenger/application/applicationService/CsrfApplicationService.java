package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisCsrfDomainService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CsrfApplicationService {

    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
    private final RedisCsrfDomainService redisCsrfDomainService;

    public HttpResponse getCsrfToken(HttpServletRequest request, HttpServletResponse response) {

        CsrfToken csrfToken = cookieCsrfTokenRepository.generateToken(request);
        cookieCsrfTokenRepository.saveToken(csrfToken, request, response);

        redisCsrfDomainService.saveExpire(csrfToken.getToken(), 3600, TimeUnit.SECONDS);

        return new HttpResponse("Csrf token successfully generated and saved in cookie", 200);
    }

}
