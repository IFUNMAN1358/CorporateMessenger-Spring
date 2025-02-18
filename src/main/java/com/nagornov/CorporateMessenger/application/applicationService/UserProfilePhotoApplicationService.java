package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.factory.UserProfilePhotoFactory;
import com.nagornov.CorporateMessenger.domain.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserProfilePhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioUserProfilePhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfilePhotoApplicationService {

    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserProfilePhotoDomainService jpaUserProfilePhotoDomainService;
    private final MinioUserProfilePhotoDomainService minioUserProfilePhotoDomainService;
    private final JwtDomainService jwtDomainService;


    @Transactional
    public UserProfilePhoto loadUserPhoto(@NotNull FileRequest request) {
        try {
            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

            User postgresUser = jpaUserDomainService.getById(
                    authInfo.getUserIdAsUUID()
            );

            UserProfilePhoto userProfilePhotoWithId = UserProfilePhotoFactory.createWithRandomId();
            userProfilePhotoWithId.updateUserId(postgresUser.getId());

            String fileName = minioUserProfilePhotoDomainService.upload(request.getFile().getInputStream());
            userProfilePhotoWithId.updateFilePath(fileName);
            userProfilePhotoWithId.markAsMain();

            Optional<UserProfilePhoto> currentUserPhoto =
                    jpaUserProfilePhotoDomainService.findMainByUserId(postgresUser.getId());
            currentUserPhoto.ifPresent(UserProfilePhoto::unmarkAsMain);

            currentUserPhoto.ifPresent(jpaUserProfilePhotoDomainService::update);
            jpaUserProfilePhotoDomainService.save(userProfilePhotoWithId);

            return jpaUserProfilePhotoDomainService.getById(userProfilePhotoWithId.getId());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load photo " + e.getMessage());
        }
    }


    @Transactional(readOnly = true)
    public Resource getMainUserPhotoByUserId(@NotNull String userId) {
        jwtDomainService.getAuthInfo();

        Optional<UserProfilePhoto> userPhoto = jpaUserProfilePhotoDomainService.findMainByUserId(
                UUID.fromString(userId)
        );

        if (userPhoto.isEmpty()) {
            throw new ResourceNotFoundException("User's main photo not found");
        }

        return new InputStreamResource(
                minioUserProfilePhotoDomainService.download(userPhoto.get().getFilePath())
        );
    }


    @Transactional(readOnly = true)
    public Resource getUserPhotoByPhotoId(@NotNull String photoId) {
        jwtDomainService.getAuthInfo();

        UserProfilePhoto userProfilePhoto = jpaUserProfilePhotoDomainService.getById(
                UUID.fromString(photoId)
        );
        return new InputStreamResource(
                minioUserProfilePhotoDomainService.download(userProfilePhoto.getFilePath())
        );
    }


    @Transactional
    public UserProfilePhoto setMainUserPhoto(@NotNull String photoId) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        User postgresUser = jpaUserDomainService.getById(
                authInfo.getUserIdAsUUID()
        );

        UserProfilePhoto newMainPhoto = jpaUserProfilePhotoDomainService.getByIdAndUserId(
                UUID.fromString(photoId),
                postgresUser.getId()
        );
        newMainPhoto.markAsMain();

        Optional<UserProfilePhoto> currentUserPhoto =
                jpaUserProfilePhotoDomainService.findMainByUserId(postgresUser.getId());
        currentUserPhoto.ifPresent(UserProfilePhoto::unmarkAsMain);

        currentUserPhoto.ifPresent(jpaUserProfilePhotoDomainService::update);
        jpaUserProfilePhotoDomainService.update(newMainPhoto);

        return jpaUserProfilePhotoDomainService.getById(newMainPhoto.getId());
    }


    @Transactional
    public HttpResponse deleteUserPhoto(@NotNull String photoId) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresUser = jpaUserDomainService.getById(
                authInfo.getUserIdAsUUID()
        );

        UserProfilePhoto photoToDelete = jpaUserProfilePhotoDomainService.getByIdAndUserId(
                UUID.fromString(photoId),
                postgresUser.getId()
        );

        if (photoToDelete.getIsMain()) {
            List<UserProfilePhoto> userProfilePhotos =
                    jpaUserProfilePhotoDomainService.getAllByUserId(postgresUser.getId())
                    .stream().filter(
                            photo -> !photo.getId().equals(photoToDelete.getId())
                    ).toList();
            if (!userProfilePhotos.isEmpty()) {
                UserProfilePhoto newMainPhoto = userProfilePhotos.getFirst();
                newMainPhoto.markAsMain();
                jpaUserProfilePhotoDomainService.update(newMainPhoto);
            }
        }

        jpaUserProfilePhotoDomainService.modDeleteById(photoToDelete.getId());
        minioUserProfilePhotoDomainService.delete(photoToDelete.getFilePath());

        return new HttpResponse("User photo deleted successfully", 200);
    }
}