package com.nagornov.CorporateMessenger.domain.service.auth;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisCsrfRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CsrfService {

    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
    private final RedisCsrfRepository redisCsrfRepository;

    //
    // COOKIE CSRF REPOSITORY
    //

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

    //
    // REDIS
    //

    public void saveToRedis(
            @NonNull String csrfToken,
            long timeout,
            @NonNull TimeUnit timeUnit
    ) {
        try {
            redisCsrfRepository.saveExpire(csrfToken, timeout, timeUnit);
        } catch (Exception e) {
            throw new ResourceConflictException("Error while saving csrf token to redis");
        }
    }

    public void deleteFromRedis(@NonNull String csrfToken) {
        try {
            redisCsrfRepository.delete(csrfToken);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error while saving csrf token to redis");
        }
    }

    public boolean existsInRedis(@NonNull String csrfToken) {
        try {
            return redisCsrfRepository.exists(csrfToken);
        } catch (Exception e) {
            throw new RuntimeException("Error while check csrf token exists in redis", e.getCause());
        }
    }

}
