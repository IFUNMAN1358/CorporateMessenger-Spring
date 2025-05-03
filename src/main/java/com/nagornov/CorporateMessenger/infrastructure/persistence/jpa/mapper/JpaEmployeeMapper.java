package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaEmployeeMapper {

    Employee toDomain(JpaEmployeeEntity entity);

    JpaEmployeeEntity toEntity(Employee domain);

}
