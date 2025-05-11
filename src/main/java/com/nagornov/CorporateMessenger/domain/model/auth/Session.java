package com.nagornov.CorporateMessenger.domain.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Session {

    private String accessToken;
    private String refreshToken;
    private String csrfToken;
    private Instant createdAt;
    private Instant updatedAt;

    public void updateAccessToken(@NonNull String newAccessToken) {
        this.accessToken = newAccessToken;
        this.updatedAt = Instant.now();
    }

    public void updateRefreshToken(@NonNull String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        this.updatedAt = Instant.now();
    }

    public void updateCsrfToken(@NonNull String csrfToken) {
        this.csrfToken = csrfToken;
        this.updatedAt = Instant.now();
    }

}
