package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserProfilePhotoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JpaUserProfilePhotoMapper {

    @Mapping(target = "userId", source = "user.id")
    UserProfilePhoto toDomain(JpaUserProfilePhotoEntity entity);

    @Mapping(target = "user.id", source = "userId")
    JpaUserProfilePhotoEntity toEntity(UserProfilePhoto domain);

}
