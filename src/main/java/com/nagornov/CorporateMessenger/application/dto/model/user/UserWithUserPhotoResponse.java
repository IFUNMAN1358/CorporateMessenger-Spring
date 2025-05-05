package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Data
public class UserWithUserPhotoResponse {

    private UserResponse user;
    private UserPhotoResponse userPhoto;

    public UserWithUserPhotoResponse(@NonNull User user, @NonNull Optional<UserPhoto> userPhoto) {
        this.user = new UserResponse(user);
        userPhoto.ifPresent(up -> this.userPhoto = new UserPhotoResponse(up));
    }

}
