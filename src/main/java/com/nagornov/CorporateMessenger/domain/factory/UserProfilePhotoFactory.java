package com.nagornov.CorporateMessenger.domain.factory;

import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class UserProfilePhotoFactory {

    public UserProfilePhoto createWithRandomId() {
        return new UserProfilePhoto(
                UUID.randomUUID(),
                null,
                null,
                false,
                LocalDateTime.now()
        );
    }

}
