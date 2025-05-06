package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeeEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeePhotoEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserSettingsEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class UserWithUserSettingsAndEmployeeAndEmployeePhotoDTOEntity implements Serializable {

    private JpaUserEntity user;
    private JpaUserSettingsEntity userSettings;
    private JpaEmployeeEntity employee;
    private Optional<JpaEmployeePhotoEntity> employeePhoto;


    public UserWithUserSettingsAndEmployeeAndEmployeePhotoDTOEntity(
            JpaUserEntity user,
            JpaUserSettingsEntity userSettings,
            JpaEmployeeEntity employee,
            JpaEmployeePhotoEntity employeePhoto
    ) {
        this.user = user;
        this.userSettings = userSettings;
        this.employee = employee;
        this.employeePhoto = Optional.ofNullable(employeePhoto);
    }
}
