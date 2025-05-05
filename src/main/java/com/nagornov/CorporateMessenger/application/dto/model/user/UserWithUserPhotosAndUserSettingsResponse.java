package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class UserWithUserPhotosAndUserSettingsResponse {

    private UserResponse user;
    private List<UserPhotoResponse> userPhotos;
    private UserSettingsResponse userSettings;


    public UserWithUserPhotosAndUserSettingsResponse(
            @NonNull User user,
            @NonNull List<UserPhoto> userPhotos,
            @NonNull UserSettings userSettings
    ) {
        this.user = new UserResponse(user);
        this.userPhotos = userPhotos.stream().map(UserPhotoResponse::new).toList();
        this.userSettings = new UserSettingsResponse(userSettings);
    }

}
