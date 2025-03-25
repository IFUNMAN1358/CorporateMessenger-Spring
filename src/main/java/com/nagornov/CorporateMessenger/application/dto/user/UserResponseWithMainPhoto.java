package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserProfilePhoto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseWithMainPhoto {

    private UserProfilePhoto userProfilePhoto;
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseWithMainPhoto(User user, UserProfilePhoto userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
