package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO {

    private User user;
    private UserSettings userSettings;
    private Employee employee;
    private Optional<EmployeePhoto> employeePhoto;

}
