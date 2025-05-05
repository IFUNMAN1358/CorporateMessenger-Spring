package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class UserWithUserPhotosResponse {

    private UserResponse user;
    private List<UserPhotoResponse> userPhotos;

    public UserWithUserPhotosResponse(@NonNull User user, @NonNull List<UserPhoto> userPhotos) {
        this.user = new UserResponse(user);
        this.userPhotos = userPhotos.stream().map(UserPhotoResponse::new).toList();
    }
}
