package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JpaRoleMapper {

    Role toDomain(JpaRoleEntity entity);

    @Mapping(target = "users", ignore = true)
    JpaRoleEntity toEntity(Role domain);

}
