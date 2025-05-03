package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserWithPhotosResponse {

    private List<UserPhoto> userPhotos;
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private Instant createdAt;
    private Instant updatedAt;

    public UserWithPhotosResponse(@NonNull User user, List<UserPhoto> userPhotos) {
        this.userPhotos = userPhotos;
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.bio = user.getBio();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
