package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository.AuthJpaSessionRepository;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaSessionAlreadyExistsException;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaSessionNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthJpaSessionService {

    private final AuthJpaSessionRepository jpaSessionRepository;

    public AuthSession createSession(@NotNull AuthUser user, @NotNull String accessToken, @NotNull String refreshToken) {
        AuthSession session = new AuthSession(
                UUID.randomUUID(),
                user.getId(),
                accessToken,
                refreshToken,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        jpaSessionRepository.findSessionById(session.getId())
                .ifPresent(e -> {
                    throw new JpaSessionAlreadyExistsException();
                });
        jpaSessionRepository.findSessionByUserId(session.getUserId())
                .ifPresent(e -> {
                    throw new JpaSessionAlreadyExistsException();
                });
        return jpaSessionRepository.save(session);
    }

    public AuthSession createOrUpdateSession(@NotNull AuthUser user, @NotNull String accessToken, @NotNull String refreshToken) {
        AuthSession session = jpaSessionRepository.findSessionByUserId(user.getId())
                .map(s -> new AuthSession(
                        s.getId(),
                        s.getUserId(),
                        accessToken,
                        refreshToken,
                        s.getCreatedAt(),
                        LocalDateTime.now()
                ))
                .orElseGet(() -> new AuthSession(
                        UUID.randomUUID(),
                        user.getId(),
                        accessToken,
                        refreshToken,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));
        return jpaSessionRepository.save(session);
    }

    public void deleteSession(@NotNull AuthSession session) {
        final AuthSession existingSession = jpaSessionRepository.findSessionById(session.getId())
                        .orElseThrow(JpaSessionNotFoundException::new);
        jpaSessionRepository.delete(existingSession);
    }

}
