package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaContactEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserBlacklistEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserSettingsEntity;
import lombok.Data;

import java.util.Optional;

@Data
public class UserWithUserSettingsAndPartnerInfoDTOEntity {

    private JpaUserEntity user;
    private JpaUserSettingsEntity userSettings;
    private Boolean isUserBlacklisted;
    private Boolean isYouBlacklisted;
    private Boolean isContact;

    public UserWithUserSettingsAndPartnerInfoDTOEntity(
            JpaUserEntity user,
            JpaUserSettingsEntity userSettings,
            JpaUserBlacklistEntity userBlacklistYouBlockedTarget,
            JpaUserBlacklistEntity userBlacklistTargetBlockedYou,
            JpaContactEntity contact1,
            JpaContactEntity contact2
    ) {
        this.user = user;
        this.userSettings = userSettings;
        this.isUserBlacklisted = Optional.ofNullable(userBlacklistYouBlockedTarget).isPresent();
        this.isYouBlacklisted = Optional.ofNullable(userBlacklistTargetBlockedYou).isPresent();
        this.isContact = Optional.ofNullable(contact1).isPresent() && Optional.ofNullable(contact2).isPresent();
    }
}
