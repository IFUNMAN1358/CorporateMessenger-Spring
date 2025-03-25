package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRegistrationKeyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaRegistrationKeyMapper {

    JpaRegistrationKeyEntity toEntity(RegistrationKey domain);

    RegistrationKey toDomain(JpaRegistrationKeyEntity entity);

}
