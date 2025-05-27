package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.user.*;
import com.nagornov.CorporateMessenger.domain.dto.*;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserBlacklist;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import com.nagornov.CorporateMessenger.domain.service.auth.SessionService;
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
    private final UserSettingsService userSettingsService;
    private final UserPhotoService userPhotoService;
    private final UserBlacklistService userBlacklistService;
    private final EmployeeService employeeService;
    private final EmployeePhotoService employeePhotoService;
    private final ContactService contactService;
    private final SessionService sessionService;
    private final PasswordService passwordService;


    @Transactional
    public void changeUserFirstNameAndLastName(@NonNull UserFirstNameAndLastNameRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        authUser.updateFirstName(request.getFirstName());
        authUser.updateLastName(request.getLastName());
        userService.update(authUser);
    }


    @Transactional
    public void changeUserUsername(@NonNull UserUsernameRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        if (userService.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException(
                    "This username already taken: %s".formatted(request.getUsername()),
                    new FieldError("newUsername", "Это имя пользователя уже занято")
            );
        }

        authUser.updateUsername(request.getUsername());
        userService.update(authUser);
    }


    @Transactional
    public void changeUserPassword(@NonNull UserPasswordRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        passwordService.ensureMatchConfirmPassword(request.getNewPassword(), request.getConfirmPassword());
        passwordService.ensureMatchEncodedPassword(request.getPassword(), authUser.getPassword());

        String encodedPassword = passwordService.encodePassword(request.getNewPassword());
        authUser.updatePassword(encodedPassword);
        userService.update(authUser);
    }


    @Transactional
    public void changeUserBio(@NonNull UserBioRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        authUser.updateBio(request.getBio());
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

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Page<UserWithUserPhotoDTO> usersDto = userService.searchWithMainUserPhotoByUsername(authInfo.getUserIdAsUUID(), username, page, pageSize);

        return usersDto.map(userDto -> new UserWithUserPhotoResponse(
                userDto.getUser(),
                userDto.getUserPhoto()
        ));
    }


    @Transactional(readOnly = true)
    public Boolean existsByUsername(@NonNull String username) {
        return userService.existsByUsername(username);
    }


    @Transactional(readOnly = true)
    public UserWithUserPhotosResponse getMyUserData() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        UserWithUserSettingsDTO userDto = userService.getWithUserSettingsById(authInfo.getUserIdAsUUID());

        List<UserPhoto> userPhotos = userPhotoService.findAllByUserId(userDto.getUser().getId());

        return new UserWithUserPhotosResponse(
                userDto.getUser(),
                userDto.getUserSettings(),
                userPhotos,
                null,
                null,
                null
        );
    }


    @Transactional(readOnly = true)
    public UserWithUserPhotosResponse getUserByUserId(@NonNull UUID userId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserWithUserSettingsAndPartnerInfoDTO targetUserDto = userService.getWithUserSettingsAndPartnerInfoByIds(
                userId,
                authInfo.getUserIdAsUUID()
        );
        List<UserPhoto> targetUserPhotos = userPhotoService.findAllByUserId(userId);

        return new UserWithUserPhotosResponse(
                targetUserDto.getUser(),
                targetUserDto.getUserSettings(),
                targetUserPhotos,
                targetUserDto.getIsUserBlacklisted(),
                targetUserDto.getIsYouBlacklisted(),
                targetUserDto.getIsContact()
        );
    }


    @Transactional(readOnly = true)
    public List<UserWithUserPhotoResponse> findAllMyBlockedUsers(int page, int size) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        List<UUID> blockedUserIds = userBlacklistService.findAllByUserId(authInfo.getUserIdAsUUID(), page, size)
                .stream().map(UserBlacklist::getBlockedUserId).toList();

        return userService.findAllWithMainUserPhotoByIds(blockedUserIds).stream().map(
                dto -> new UserWithUserPhotoResponse(
                        dto.getUser(),
                        dto.getUserPhoto()
                )
        ).toList();
    }


    @Transactional
    public void blockUserByUserId(@NonNull UUID userId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), userId);
        User authUser = userPairDTO.getUser1();
        User targetUser = userPairDTO.getUser2();

        if (userBlacklistService.existsByUserIdAndBlockedUserId(authUser.getId(), targetUser.getId())) {
            throw new ResourceBadRequestException("User already blocked");
        }

        contactService.deleteContactPairByUserIds(authUser.getId(), targetUser.getId());
        userBlacklistService.create(authUser.getId(), targetUser.getId());
    }


    @Transactional
    public void unblockUserByUserId(@NonNull UUID userId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), userId);
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

        sessionService.deleteAllFromRedis(authUser.getId());
    }
}