package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.model.employee.EmployeePhotoResponse;
import com.nagornov.CorporateMessenger.domain.dto.EmployeeWithEmployeePhotoDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeePhotoApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserBlacklistService userBlacklistService;
    private final ContactService contactService;
    private final UserSettingsService userSettingsService;
    private final EmployeeService employeeService;
    private final EmployeePhotoService employeePhotoService;


    @Transactional
    public EmployeePhotoResponse uploadOrUpdateMyEmployeePhoto(@NonNull FileRequest request) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        EmployeeWithEmployeePhotoDTO empDto = employeeService.getWithEmployeePhotoByUserId(authInfo.getUserIdAsUUID());
        Employee employee = empDto.getEmployee();
        Optional<EmployeePhoto> optEmployeePhoto = empDto.getEmployeePhoto();

        if (optEmployeePhoto.isPresent()) {
            employeePhotoService.deleteByEmployeeId(employee.getId());
            EmployeePhoto newEmployeePhoto = employeePhotoService.upload(employee.getId(), request.getFile());
            return new EmployeePhotoResponse(newEmployeePhoto);
        }
        EmployeePhoto newEmployeePhoto = employeePhotoService.upload(employee.getId(), request.getFile());
        return new EmployeePhotoResponse(newEmployeePhoto);
    }


    @Transactional(readOnly = true)
    public Resource downloadMyEmployeePhoto(@NonNull String size) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        EmployeeWithEmployeePhotoDTO empDto = employeeService.getWithEmployeePhotoByUserId(authInfo.getUserIdAsUUID());
        Optional<EmployeePhoto> optEmployeePhoto = empDto.getEmployeePhoto();

        if (optEmployeePhoto.isEmpty()) {
            return null;
        }

        if (size.equals("big")) {
            return employeePhotoService.download(optEmployeePhoto.get().getBigFilePath());
        } else if (size.equals("small")) {
            return employeePhotoService.download(optEmployeePhoto.get().getSmallFilePath());
        } else {
            throw new ResourceConflictException("Invalid RequestParam(size) to downloading employee photo.");
        }
    }


    @Transactional(readOnly = true)
    public Resource downloadEmployeePhotoByUserId(@NonNull UUID userId, @NonNull String size) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO targetDto =
                userService.getWithUserSettingsAndEmployeeAndEmployeePhotoById(userId);

        User targetUser = targetDto.getUser();
        UserSettings targetUserSettings = targetDto.getUserSettings();
        Optional<EmployeePhoto> targetOptEmployeePhoto = targetDto.getEmployeePhoto();

        userBlacklistService.ensureUserNotBlocked(targetUser.getId(), authInfo.getUserIdAsUUID());

        boolean existsContactPair = contactService.existsContactPairByUserIds(authInfo.getUserIdAsUUID(), targetUser.getId());

        userSettingsService.ensureUserHasAccessToProfile(targetUserSettings, existsContactPair);
        userSettingsService.ensureUserHasAccessToEmployee(targetUserSettings, existsContactPair);

        if (targetOptEmployeePhoto.isEmpty()) {
            return null;
        }

        if (size.equals("big")) {
            return employeePhotoService.download(targetOptEmployeePhoto.get().getBigFilePath());
        } else if (size.equals("small")) {
            return employeePhotoService.download(targetOptEmployeePhoto.get().getSmallFilePath());
        } else {
            throw new ResourceConflictException("Invalid RequestParam(size) to downloading employee photo.");
        }
    }


    @Transactional
    public void deleteMyEmployeePhoto() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        EmployeeWithEmployeePhotoDTO empDto = employeeService.getWithEmployeePhotoByUserId(authInfo.getUserIdAsUUID());
        Employee employee = empDto.getEmployee();
        Optional<EmployeePhoto> optEmployeePhoto = empDto.getEmployeePhoto();

        if (optEmployeePhoto.isPresent()) {
            employeePhotoService.deleteByEmployeeId(employee.getId());
        }
    }
}