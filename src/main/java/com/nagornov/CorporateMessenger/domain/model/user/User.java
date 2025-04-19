package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String password;
    private String phone;
    private String mainEmail;
    private Instant createdAt;
    private Instant updatedAt;

    public void updateUsername(@NonNull String newUsername) {
        this.username = newUsername;
        this.updatedAt = Instant.now();
    }

    public void updatePassword(@NonNull String newPassword) {
        this.password = newPassword;
        this.updatedAt = Instant.now();
    }

    public void updatePhone(@NonNull String newPhone) {
        this.phone = newPhone;
        this.updatedAt = Instant.now();
    }

    public void updateMainEmail(@NonNull String newMainEmail) {
        this.mainEmail = newMainEmail;
        this.updatedAt = Instant.now();
    }

}