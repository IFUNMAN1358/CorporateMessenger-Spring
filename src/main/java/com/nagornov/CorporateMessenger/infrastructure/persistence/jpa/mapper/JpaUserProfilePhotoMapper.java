package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.UserProfilePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserProfilePhotoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JpaUserProfilePhotoMapper {

    UserProfilePhoto toDomain(JpaUserProfilePhotoEntity entity);

    JpaUserProfilePhotoEntity toEntity(UserProfilePhoto domain);

}
