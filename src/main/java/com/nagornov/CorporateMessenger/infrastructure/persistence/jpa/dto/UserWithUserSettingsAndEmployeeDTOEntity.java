package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeeEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserSettingsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithUserSettingsAndEmployeeDTOEntity {

    private JpaUserEntity user;
    private JpaUserSettingsEntity userSettings;
    private JpaEmployeeEntity employee;

}
