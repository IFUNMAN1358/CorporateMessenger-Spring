package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithEmployeeDTO {

    private User user;
    private Employee employee;

}
