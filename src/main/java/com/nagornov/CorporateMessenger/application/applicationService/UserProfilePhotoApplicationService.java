package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.factory.UserProfilePhotoFactory;
import com.nagornov.CorporateMessenger.domain.logger.ApplicationServiceLogger;
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
    private final ApplicationServiceLogger applicationServiceLogger;


    @Transactional
    public UserProfilePhoto loadUserProfilePhoto(@NotNull FileRequest request) {
        try {
            applicationServiceLogger.info("Load user profile photo started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    authInfo.getUserIdAsUUID()
            );

            String fileName = minioUserProfilePhotoDomainService.upload(request.getFile().getInputStream());

            UserProfilePhoto userProfilePhotoWithId = UserProfilePhotoFactory.createWithRandomId();
            userProfilePhotoWithId.updateUserId(postgresUser.getId());
            userProfilePhotoWithId.updateFilePath(fileName);
            userProfilePhotoWithId.markAsMain();

            Optional<UserProfilePhoto> currentUserPhoto =
                    jpaUserProfilePhotoDomainService.findMainByUserId(postgresUser.getId());
            currentUserPhoto.ifPresent(UserProfilePhoto::unmarkAsMain);

            currentUserPhoto.ifPresent(jpaUserProfilePhotoDomainService::update);
            jpaUserProfilePhotoDomainService.save(userProfilePhotoWithId);

            applicationServiceLogger.info("Load user profile photo started");

            UserProfilePhoto newMainUserProfilePhoto =
                    jpaUserProfilePhotoDomainService.getById(userProfilePhotoWithId.getId());

            applicationServiceLogger.info("Load user profile photo finished");

            return newMainUserProfilePhoto;

        } catch (Exception e) {
            applicationServiceLogger.error("Load user profile photo failed", e);
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }


    @Transactional(readOnly = true)
    public Resource getMainUserProfilePhotoByUserId(@NotNull String userId) {
        try {
            applicationServiceLogger.info("Get user profile photo started");

            jwtDomainService.getAuthInfo();

            Optional<UserProfilePhoto> userPhoto = jpaUserProfilePhotoDomainService.findMainByUserId(
                    UUID.fromString(userId)
            );

            if (userPhoto.isEmpty()) {
                throw new ResourceNotFoundException("User's main photo not found");
            }

            applicationServiceLogger.info("Get user profile photo finished");

            return new InputStreamResource(
                    minioUserProfilePhotoDomainService.download(userPhoto.get().getFilePath())
            );

        } catch (Exception e) {
            applicationServiceLogger.error("Get user profile photo failed", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public Resource getUserProfilePhotoByPhotoId(@NotNull String photoId) {
        try {
            applicationServiceLogger.info("Get user profile photo by id started");

            jwtDomainService.getAuthInfo();

            UserProfilePhoto userProfilePhoto = jpaUserProfilePhotoDomainService.getById(
                    UUID.fromString(photoId)
            );

            applicationServiceLogger.info("Get user profile photo by id finished");

            return new InputStreamResource(
                    minioUserProfilePhotoDomainService.download(userProfilePhoto.getFilePath())
            );

        } catch (Exception e) {
            applicationServiceLogger.error("Get user profile photo by id failed", e);
            throw e;
        }
    }


    @Transactional
    public UserProfilePhoto setMainUserProfilePhoto(@NotNull String photoId) {
        try {
            applicationServiceLogger.info("Set user profile photo as main started");

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

            UserProfilePhoto newMainUserProfilePhoto = jpaUserProfilePhotoDomainService.getById(newMainPhoto.getId());

            applicationServiceLogger.info("Set user profile photo as main finished");

            return newMainUserProfilePhoto;

        } catch (Exception e) {
            applicationServiceLogger.error("Set user profile photo as main failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse deleteUserProfilePhoto(@NotNull String photoId) {
        try {
            applicationServiceLogger.info("Delete user profile photo started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
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

            applicationServiceLogger.info("Delete user profile photo finished");

            return new HttpResponse("User photo deleted successfully", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Delete user profile photo failed", e);
            throw e;
        }
    }
}