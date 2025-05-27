package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class UserWithUserPhotosResponse {

    private UserResponse user;
    private UserSettingsResponse userSettings;
    private List<UserPhotoResponse> userPhotos;
    private Boolean isUserBlacklisted;
    private Boolean isYouBlacklisted;
    private Boolean isContact;

    public UserWithUserPhotosResponse(
            @NonNull User user,
            @NonNull UserSettings userSettings,
            @NonNull List<UserPhoto> userPhotos,
            Boolean isUserBlacklisted,
            Boolean isYouBlacklisted,
            Boolean isContact
    ) {
        this.user = new UserResponse(user);
        this.userSettings = new UserSettingsResponse(userSettings);
        this.userPhotos = userPhotos.stream().map(UserPhotoResponse::new).toList();
        this.isUserBlacklisted = isUserBlacklisted;
        this.isYouBlacklisted = isYouBlacklisted;
        this.isContact = isContact;
    }
}
