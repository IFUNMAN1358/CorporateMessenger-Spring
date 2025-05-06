package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserPhotoEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class UserWithUserPhotoDTOEntity implements Serializable {

    private JpaUserEntity user;
    private Optional<JpaUserPhotoEntity> userPhoto;

    public UserWithUserPhotoDTOEntity(JpaUserEntity user, JpaUserPhotoEntity userPhoto) {
        this.user = user;
        this.userPhoto = Optional.ofNullable(userPhoto);
    }

}
