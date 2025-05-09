package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.user.*;
import com.nagornov.CorporateMessenger.domain.dto.UserPairDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserWithEmployeeDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserWithUserPhotoDTO;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtSessionService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.auth.PasswordService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private final UserBlacklistService userBlacklistService;
    private final EmployeeService employeeService;
    private final EmployeePhotoService employeePhotoService;
    private final ContactService contactService;
    private final JwtSessionService jwtSessionService;
    private final PasswordService passwordService;


    @Transactional
    public void changeUserUsername(@NonNull UserUsernameRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        if (userService.existsByUsername(request.getNewUsername())) {
            throw new ResourceConflictException(
                    "This username already taken: %s".formatted(request.getNewUsername()),
                    new FieldError("newUsername", "Это имя пользователя уже занято")
            );
        }

        authUser.updateUsername(request.getNewUsername());
        userService.update(authUser);
    }


    @Transactional
    public void changeUserPassword(@NonNull UserPasswordRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        passwordService.matchEncodedPassword(request.getCurrentPassword(), authUser.getPassword());

        String encodedPassword = passwordService.encodePassword(request.getNewPassword());
        authUser.updatePassword(encodedPassword);
        userService.update(authUser);
    }


    @Transactional
    public void changeUserMainEmail() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());
    }


    @Transactional
    public void changeUserPhone() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());
    }


    @Transactional(readOnly = true)
    public Page<UserWithUserPhotoResponse> searchUsersByUsername(@NonNull String username, int page, int pageSize) {

        jwtService.getAuthInfo();

        Page<UserWithUserPhotoDTO> usersDto = userService.searchWithMainUserPhotoByUsername(username, page, pageSize);

        return usersDto.map(userDto -> new UserWithUserPhotoResponse(
                userDto.getUser(),
                userDto.getUserPhoto()
        ));
    }


    @Transactional(readOnly = true)
    public UserWithUserPhotosResponse getYourUserData() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        List<UserPhoto> userPhotos = userPhotoService.findAllByUserId(user.getId());

        return new UserWithUserPhotosResponse(user, userPhotos);
    }


    @Transactional(readOnly = true)
    public UserWithUserPhotosResponse getUserById(@NonNull UUID userId) {

        jwtService.getAuthInfo();

        User targetUser = userService.getById(userId);
        List<UserPhoto> targetUserPhotos = userPhotoService.findAllByUserId(userId);

        return new UserWithUserPhotosResponse(targetUser, targetUserPhotos);
    }


    @Transactional
    public void blockUserByUserId(@NonNull UserIdRequest request) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), request.getUserId());
        User authUser = userPairDTO.getUser1();
        User targetUser = userPairDTO.getUser2();

        if (userBlacklistService.existsByUserIdAndBlockedUserId(authUser.getId(), targetUser.getId())) {
            throw new ResourceBadRequestException("User already blocked");
        }

        contactService.deleteContactPairByUserIds(authUser.getId(), targetUser.getId());
        userBlacklistService.create(authUser.getId(), targetUser.getId());
    }


    @Transactional
    public void unblockUserByUserId(@NonNull UserIdRequest request) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), request.getUserId());
        User authUser = userPairDTO.getUser1();
        User targetUser = userPairDTO.getUser2();

        if (!userBlacklistService.existsByUserIdAndBlockedUserId(authUser.getId(), targetUser.getId())) {
            throw new ResourceBadRequestException("User not blocked");
        }

        userBlacklistService.deleteByUserIdAndBlockedUserId(authUser.getId(), targetUser.getId());
    }


    @Transactional
    public void softDeleteAccount() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserWithEmployeeDTO authUserDto = userService.getWithEmployeeById(authInfo.getUserIdAsUUID());
        User authUser = authUserDto.getUser();
        Employee authEmployee = authUserDto.getEmployee();

        authUser.updateUsername("D_%s".formatted(authUser.getId().toString().substring(0, 30)));
        authUser.updateFirstName("Пользователь удалён");
        authUser.updateLastName(null);
        authUser.updateBio(null);
        authUser.updateMainEmail(null);
        authUser.updatePhone(null);
        authUser.markAsDeleted();
        userService.update(authUser);

        authEmployee.updateLeaderId(null);
        authEmployee.updateDepartment(null);
        authEmployee.updatePosition(null);
        authEmployee.updateDescription(null);
        authEmployee.updateWorkSchedule(null);
        employeeService.update(authEmployee);

        userPhotoService.deleteAllByUserId(authUser.getId());
        employeePhotoService.deleteByEmployeeId(authEmployee.getId());

        jwtSessionService.deleteFromRedis(authUser.getId());
    }
}