package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.employee.EmployeeWithEmployeePhotoResponse;
import com.nagornov.CorporateMessenger.application.dto.model.employee.UpdateEmployeeRequest;
import com.nagornov.CorporateMessenger.domain.dto.EmployeeWithEmployeePhotoDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO;
import com.nagornov.CorporateMessenger.domain.enums.model.EmployeeVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.ProfileVisibility;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final EmployeePhotoService employeePhotoService;
    private final UserBlacklistService userBlacklistService;
    private final ContactService contactService;


    @Transactional(readOnly = true)
    public EmployeeWithEmployeePhotoResponse getMyEmployee() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        EmployeeWithEmployeePhotoDTO dto = employeeService.getWithEmployeePhotoByUserId(authInfo.getUserIdAsUUID());
        return new EmployeeWithEmployeePhotoResponse(dto.getEmployee(), dto.getEmployeePhoto());
    }


    @Transactional(readOnly = true)
    public EmployeeWithEmployeePhotoResponse getEmployeeByUserId(@NonNull UUID userId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO targetDto =
                userService.getWithUserSettingsAndEmployeeAndEmployeePhotoById(userId);

        User targetUser = targetDto.getUser();
        UserSettings targetUserSettings = targetDto.getUserSettings();
        Employee targetEmployee = targetDto.getEmployee();
        Optional<EmployeePhoto> optTargetEmployeePhoto = targetDto.getEmployeePhoto();

        if (userBlacklistService.existsByUserIdAndBlockedUserId(targetUser.getId(), authInfo.getUserIdAsUUID())) {
            throw new ResourceBadRequestException("User has blocked you");
        }

        boolean existsContactPair = contactService.existsContactPairByUserIds(authInfo.getUserIdAsUUID(), targetUser.getId());

        if (
                targetUserSettings.isProfileVisibility(ProfileVisibility.ONLY_ME) ||
                (targetUserSettings.isProfileVisibility(ProfileVisibility.CONTACTS) && !existsContactPair)
        ) {
            throw new ResourceBadRequestException("You can't get employee data");
        }

        if (
                targetUserSettings.isEmployeeVisibility(EmployeeVisibility.ONLY_ME) ||
                (targetUserSettings.isEmployeeVisibility(EmployeeVisibility.CONTACTS) && !existsContactPair)
        ) {
            throw new ResourceBadRequestException("You can't get employee data");
        }

        return new EmployeeWithEmployeePhotoResponse(targetEmployee, optTargetEmployeePhoto);
    }


    @Transactional
    public EmployeeWithEmployeePhotoResponse updateMyEmployee(@NonNull UpdateEmployeeRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Employee employee = employeeService.getByUserId(authInfo.getUserIdAsUUID());

        if (request.getNewLeaderId() != null) {
            employee.updateLeaderId(request.getNewLeaderId());
        }
        if (request.getNewDepartment() != null) {
            employee.updateDepartment(request.getNewDepartment());
        }
        if (request.getNewPosition() != null) {
            employee.updatePosition(request.getNewPosition());
        }
        if (request.getNewDescription() != null) {
            employee.updateDescription(request.getNewDescription());
        }
        if (request.getNewWorkSchedule() != null) {
            employee.updateWorkSchedule(request.getNewWorkSchedule());
        }
        Employee updEmployee = employeeService.update(employee);
        Optional<EmployeePhoto> optEmpPhoto = employeePhotoService.findByEmployeeId(updEmployee.getId());

        return new EmployeeWithEmployeePhotoResponse(updEmployee, optEmpPhoto);
    }


    @Transactional
    public EmployeeWithEmployeePhotoResponse clearMyEmployee() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Employee employee = employeeService.getByUserId(authInfo.getUserIdAsUUID());

        employee.updateLeaderId(null);
        employee.updateDepartment(null);
        employee.updatePosition(null);
        employee.updateDescription(null);
        employee.updateWorkSchedule(null);
        Employee updEmployee = employeeService.update(employee);

        employeePhotoService.deleteByEmployeeId(updEmployee.getId());

        return new EmployeeWithEmployeePhotoResponse(updEmployee, Optional.empty());
    }
}