package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserSettingsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UserWithUserSettingsDTOEntity implements Serializable {

    private JpaUserEntity user;
    private JpaUserSettingsEntity userSettings;

}
