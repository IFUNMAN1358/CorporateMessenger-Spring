package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthRegistrationKey;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaRegistrationKeyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthJpaRegistrationKeyMapper {

    AuthJpaRegistrationKeyEntity toEntity(AuthRegistrationKey domain);

    AuthRegistrationKey toDomain(AuthJpaRegistrationKeyEntity entity);

}
