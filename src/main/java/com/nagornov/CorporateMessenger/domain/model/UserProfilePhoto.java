package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserProfilePhoto {

    private UUID id;
    private UUID userId;
    private String filePath;
    private Boolean isMain = false;
    private LocalDateTime createdAt;

    public void updateUserId(@NotNull UUID newUserId) {
        this.userId = newUserId;
    }

    public void updateFilePath(@NotNull String newFilePath) {
        this.filePath = newFilePath;
    }

    public void markAsMain() {
        this.isMain = true;
    }

    public void unmarkAsMain() {
        this.isMain = false;
    }

}