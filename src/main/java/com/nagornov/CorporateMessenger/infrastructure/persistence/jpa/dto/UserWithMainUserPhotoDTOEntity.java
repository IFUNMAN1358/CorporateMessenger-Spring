package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserPhotoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithMainUserPhotoDTOEntity {

    private JpaUserEntity user;
    private JpaUserPhotoEntity mainUserPhoto;

}
