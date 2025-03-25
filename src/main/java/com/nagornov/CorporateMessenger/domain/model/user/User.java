package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //
    //
    //

    public void updateUsername(@NonNull String newUsername) {
        this.username = newUsername;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(@NonNull String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateFirstName(@NonNull String newFirstName) {
        this.firstName = newFirstName;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastName(@NonNull String newLastName) {
        this.lastName = newLastName;
        this.updatedAt = LocalDateTime.now();
    }

}