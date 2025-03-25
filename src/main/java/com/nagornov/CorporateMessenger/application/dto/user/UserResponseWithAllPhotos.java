package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserProfilePhoto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserResponseWithAllPhotos {

    private List<UserProfilePhoto> userProfilePhotos;
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseWithAllPhotos(User user, List<UserProfilePhoto> userProfilePhotos) {
        this.userProfilePhotos = userProfilePhotos;
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
