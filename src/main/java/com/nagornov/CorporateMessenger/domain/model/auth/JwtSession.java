package com.nagornov.CorporateMessenger.domain.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class JwtSession {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateAccessToken(@NonNull String newAccessToken) {
        this.accessToken = newAccessToken;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRefreshToken(@NonNull String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        this.updatedAt = LocalDateTime.now();
    }

}
