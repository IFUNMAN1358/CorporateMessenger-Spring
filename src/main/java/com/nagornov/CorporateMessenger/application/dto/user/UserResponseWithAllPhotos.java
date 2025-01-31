package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import jakarta.validation.constraints.NotNull;
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

    public UserResponseWithAllPhotos(@NotNull User user, @NotNull List<UserProfilePhoto> userProfilePhotos) {
        this.userProfilePhotos = userProfilePhotos;
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
