package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponse(@NonNull User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.bio = user.getBio();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
