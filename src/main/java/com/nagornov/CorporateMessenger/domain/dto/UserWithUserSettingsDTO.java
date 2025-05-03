package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserWithUserSettingsDTO {

    private User user;
    private UserSettings userSettings;

}
