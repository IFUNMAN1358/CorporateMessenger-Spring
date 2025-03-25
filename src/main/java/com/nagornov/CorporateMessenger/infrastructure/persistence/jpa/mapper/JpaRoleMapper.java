package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaRoleMapper {

    Role toDomain(JpaRoleEntity entity);

    JpaRoleEntity toEntity(Role domain);

}
