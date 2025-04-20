package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseWithMainPhoto {

    private UserPhoto userPhoto;
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponseWithMainPhoto(User user, UserPhoto userPhoto) {
        this.userPhoto = userPhoto;
        this.id = user.getId();
        this.username = user.getUsername();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
