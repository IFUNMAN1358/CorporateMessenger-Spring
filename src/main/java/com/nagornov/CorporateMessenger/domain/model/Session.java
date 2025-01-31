package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Session {

    private UUID id;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateAccessToken(@NotNull String newAccessToken) {
        this.accessToken = newAccessToken;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRefreshToken(@NotNull String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        this.updatedAt = LocalDateTime.now();
    }

}
