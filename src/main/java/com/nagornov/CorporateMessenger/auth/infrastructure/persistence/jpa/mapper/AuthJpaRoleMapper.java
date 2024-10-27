package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthRole;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthJpaRoleMapper {

    AuthRole toDomain(AuthJpaRoleEntity entity);

    @Mapping(target = "users", ignore = true)
    AuthJpaRoleEntity toEntity(AuthRole domain);

}
