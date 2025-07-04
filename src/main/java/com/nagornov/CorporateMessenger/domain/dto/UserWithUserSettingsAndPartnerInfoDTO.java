package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithUserSettingsAndPartnerInfoDTO {

    private User user;
    private UserSettings userSettings;
    private Boolean isUserBlacklisted;
    private Boolean isYouBlacklisted;
    private Boolean isContact;

}
