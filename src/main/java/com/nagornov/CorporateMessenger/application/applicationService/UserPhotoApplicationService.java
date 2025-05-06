package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserPhotoResponse;
import com.nagornov.CorporateMessenger.domain.dto.UserWithUserSettingsAndUserPhotoDTO;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPhotoApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private final UserSettingsService userSettingsService;
    private final ContactService contactService;
    private final UserBlacklistService userBlacklistService;


    @Transactional
    public UserPhotoResponse loadMyUserPhoto(@NonNull FileRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPhoto newMainUserPhoto = userPhotoService.upload(authInfo.getUserIdAsUUID(), request.getFile());

        Optional<UserPhoto> currentUserPhoto = userPhotoService.findMainByUserId(authInfo.getUserIdAsUUID());
        currentUserPhoto.ifPresent(UserPhoto::unmarkAsMain);
        currentUserPhoto.ifPresent(userPhotoService::update);

        return new UserPhotoResponse(newMainUserPhoto);
    }


    @Transactional(readOnly = true)
    public Resource downloadMyMainUserPhoto(@NonNull String size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Optional<UserPhoto> optMainUserPhoto = userPhotoService.findMainByUserId(authInfo.getUserIdAsUUID());

        if (optMainUserPhoto.isEmpty()) {
            return null;
        }

        if (size.equals("big")) {
            return userPhotoService.download(optMainUserPhoto.get().getBigFilePath());
        } else if (size.equals("small")) {
            return userPhotoService.download(optMainUserPhoto.get().getSmallFilePath());
        } else {
            throw new ResourceConflictException("Invalid RequestParam(size) type for downloading main user photo");
        }
    }


    @Transactional(readOnly = true)
    public Resource downloadMyUserPhotoByPhotoId(@NonNull UUID photoId, @NonNull String size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPhoto userPhoto = userPhotoService.getByIdAndUserId(photoId, authInfo.getUserIdAsUUID());

        if (size.equals("big")) {
            return userPhotoService.download(userPhoto.getBigFilePath());
        } else if (size.equals("small")) {
            return userPhotoService.download(userPhoto.getSmallFilePath());
        } else {
            throw new ResourceConflictException("Invalid RequestParam(size) type for downloading main user photo");
        }
    }


    @Transactional(readOnly = true)
    public Resource downloadMainUserPhotoByUserId(@NonNull UUID userId, @NonNull String size) {

        jwtService.getAuthInfo();

        Optional<UserPhoto> targetOptMainUserPhoto = userPhotoService.findMainByUserId(userId);

        if (targetOptMainUserPhoto.isEmpty()) {
            return null;
        }

        if (size.equals("big")) {
            return userPhotoService.download(targetOptMainUserPhoto.get().getBigFilePath());
        } else if (size.equals("small")) {
            return userPhotoService.download(targetOptMainUserPhoto.get().getSmallFilePath());
        } else {
            throw new ResourceConflictException("Invalid RequestParam(size) type for downloading main user photo");
        }
    }


    @Transactional(readOnly = true)
    public Resource downloadUserPhotoByUserIdAndPhotoId(@NonNull UUID userId, @NonNull UUID photoId, @NonNull String size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserWithUserSettingsAndUserPhotoDTO targetDto =
                userService.getWithUserSettingsAndUserPhotoByIdAndPhotoId(userId, photoId);
        User targetUser = targetDto.getUser();
        UserSettings targetUserSettings = targetDto.getUserSettings();
        UserPhoto targetUserPhoto = targetDto.getUserPhoto();

        userBlacklistService.ensureUserNotBlocked(targetUser.getId(), authInfo.getUserIdAsUUID());

        boolean existsContactPair = contactService.existsContactPairByUserIds(authInfo.getUserIdAsUUID(), targetUser.getId());

        userSettingsService.ensureUserHasAccessToProfile(targetUserSettings, existsContactPair);

        if (size.equals("big")) {
            return userPhotoService.download(targetUserPhoto.getBigFilePath());
        } else if (size.equals("small")) {
            return userPhotoService.download(targetUserPhoto.getSmallFilePath());
        } else {
            throw new ResourceConflictException("Invalid RequestParam(size) type for downloading main user photo");
        }
    }


    @Transactional
    public UserPhotoResponse setMyMainUserPhoto(@NonNull UUID photoId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPhoto currentMainUserPhoto = userPhotoService.getMainByUserId(authInfo.getUserIdAsUUID());
        currentMainUserPhoto.unmarkAsMain();

        UserPhoto newMainUserPhoto = userPhotoService.getByIdAndUserId(photoId, authInfo.getUserIdAsUUID());
        newMainUserPhoto.markAsMain();

        userPhotoService.updateAll(List.of(currentMainUserPhoto, newMainUserPhoto));

        return new UserPhotoResponse(newMainUserPhoto);
    }


    @Transactional
    public void deleteMyUserPhoto(@NonNull UUID photoId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPhoto userPhotoToDelete = userPhotoService.getByIdAndUserId(photoId, authInfo.getUserIdAsUUID());

        List<UserPhoto> userPhotos = userPhotoService.findAllByUserId(authInfo.getUserIdAsUUID());

        if (userPhotoToDelete.getIsMain()) {
            for (UserPhoto userPhoto : userPhotos) {
                if (!userPhoto.getId().equals(userPhotoToDelete.getId())) {
                    userPhoto.markAsMain();
                    userPhotoService.update(userPhoto);
                    break;
                }
            }
        }
        userPhotoService.delete(userPhotoToDelete);
    }
}