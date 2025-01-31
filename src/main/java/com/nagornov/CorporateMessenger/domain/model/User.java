package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
    private Set<Role> roles;
    private Set<UserProfilePhoto> userProfilePhotos;

    public void setRandomId() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    public void updateUsername(@NotNull String newUsername) {
        this.username = newUsername;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(@NotNull String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(@NotNull Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRole(@NotNull Role role) {
        this.roles.remove(role);
        this.updatedAt = LocalDateTime.now();
    }

}