package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.UserRole;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserRoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaUserRoleMapper {

    UserRole toDomain(JpaUserRoleEntity entity);

    JpaUserRoleEntity toEntity(UserRole domain);

}
