package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Data
public class UserWithUserPhotoAndUserSettingsResponse {

    private UserResponse user;
    private UserPhotoResponse userPhoto;
    private UserSettingsResponse userSettings;


    public UserWithUserPhotoAndUserSettingsResponse(
            @NonNull User user,
            @NonNull Optional<UserPhoto> userPhoto,
            @NonNull UserSettings userSettings
    ) {
        this.user = new UserResponse(user);
        userPhoto.ifPresent(up -> this.userPhoto = new UserPhotoResponse(up));
        this.userSettings = new UserSettingsResponse(userSettings);
    }

}
