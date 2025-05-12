package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaExternalServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaExternalServiceMapper {

    ExternalService toDomain(JpaExternalServiceEntity entity);

    JpaExternalServiceEntity toEntity(ExternalService domain);

}
