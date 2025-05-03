package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeeEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithEmployeeDTOEntity {

    private JpaUserEntity user;
    private JpaEmployeeEntity employee;

}
