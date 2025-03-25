package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaUserMapper {

    User toDomain(JpaUserEntity entity);

    JpaUserEntity toEntity(User domain);

}