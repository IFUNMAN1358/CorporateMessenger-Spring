package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.Role;
import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserProfilePhotoEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JpaUserMapper {

    //
    // TO DOMAIN
    //

    @Mapping(target = "roles", source = "entity.roles")
    @Mapping(target = "userProfilePhotos", source = "entity.userProfilePhotos")
    User toDomain(JpaUserEntity entity);

    default Set<Role> mapRoles(Set<JpaRoleEntity> jpaRoleEntities) {
        if (jpaRoleEntities == null) {
            return null;
        }
        return jpaRoleEntities.stream()
                .map(jpaRoleEntity -> new Role(
                        jpaRoleEntity.getId(),
                        jpaRoleEntity.getName(),
                        jpaRoleEntity.getCreatedAt()
                )).collect(Collectors.toSet());
    }

    default Set<UserProfilePhoto> mapUserProfilePhotos(Set<JpaUserProfilePhotoEntity> jpaUserPhotoEntities) {
        if (jpaUserPhotoEntities == null) {
            return null;
        }
        return jpaUserPhotoEntities.stream()
                .map(JpaUserProfilePhotoEntity -> new UserProfilePhoto(
                        JpaUserProfilePhotoEntity.getId(),
                        JpaUserProfilePhotoEntity.getUser().getId(),
                        JpaUserProfilePhotoEntity.getFilePath(),
                        JpaUserProfilePhotoEntity.getIsMain(),
                        JpaUserProfilePhotoEntity.getCreatedAt()
                )).collect(Collectors.toSet());
    }

    //
    // TO ENTITY
    //

    @Mapping(target = "roles", source = "user.roles")
    @Mapping(target = "userProfilePhotos", expression = "java(toUserPhotoEntities(user.getUserProfilePhotos(), userEntity))")
    JpaUserEntity toEntity(User user, @Context JpaUserEntity userEntity);

    default Set<JpaRoleEntity> toRoleEntities(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> new JpaRoleEntity(
                        role.getId(),
                        role.getName(),
                        role.getCreatedAt(),
                        null
                ))
                .collect(Collectors.toSet());
    }

    default Set<JpaUserProfilePhotoEntity> toUserPhotoEntities(Set<UserProfilePhoto> userProfilePhotos, @Context JpaUserEntity userEntity) {
        if (userProfilePhotos == null || userEntity == null) {
            return null;
        }
        return userProfilePhotos.stream()
                .map(userPhoto -> {
                    JpaUserProfilePhotoEntity photoEntity = new JpaUserProfilePhotoEntity();
                    photoEntity.setId(userPhoto.getId());
                    photoEntity.setUser(userEntity);
                    photoEntity.setFilePath(userPhoto.getFilePath());
                    photoEntity.setIsMain(userPhoto.getIsMain());
                    photoEntity.setCreatedAt(userPhoto.getCreatedAt());
                    return photoEntity;
                })
                .collect(Collectors.toSet());
    }
}