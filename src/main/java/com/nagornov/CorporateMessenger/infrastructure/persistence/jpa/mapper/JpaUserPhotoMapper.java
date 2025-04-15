package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserPhotoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaUserPhotoMapper {

    UserPhoto toDomain(JpaUserPhotoEntity entity);

    JpaUserPhotoEntity toEntity(UserPhoto domain);

}
