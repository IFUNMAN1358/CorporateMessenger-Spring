package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.logger.ApplicationServiceLogger;
import com.nagornov.CorporateMessenger.domain.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserProfilePhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioUserProfilePhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisSessionDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.PasswordDomainService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDataApplicationService {

    private final JwtDomainService jwtDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserProfilePhotoDomainService jpaUserProfilePhotoDomainService;
    private final MinioUserProfilePhotoDomainService minioUserProfilePhotoDomainService;
    private final RedisSessionDomainService redisSessionDomainService;
    private final PasswordDomainService passwordDomainService;
    private final ApplicationServiceLogger applicationServiceLogger;


    @Transactional
    public HttpResponse changeUserPassword(@NotNull PasswordRequest request) {
        try {
            applicationServiceLogger.info("Change user password started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    UUID.fromString(authInfo.getUserId())
            );

            passwordDomainService.matchPassword(request.getCurrentPassword(), postgresUser.getPassword());

            String encodedPassword = passwordDomainService.encodePassword(request.getNewPassword());
            postgresUser.updatePassword(encodedPassword);
            jpaUserDomainService.update(postgresUser);

            applicationServiceLogger.info("Change user password finished");

            return new HttpResponse("Password changed", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Change user password failed", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public List<UserResponseWithMainPhoto> searchUsersByUsername(
            @NotNull String username,
            @NotNull int page,
            @NotNull int pageSize
    ) {
        try {
            applicationServiceLogger.info("Search users by username started");

            jwtDomainService.getAuthInfo();

            List<User> userList = jpaUserDomainService.searchByUsername(username, page, pageSize);

            applicationServiceLogger.info("Search users by username finished");

            return userList.stream().map(user -> {
                Optional<UserProfilePhoto> currentUserPhoto = jpaUserProfilePhotoDomainService.findMainByUserId(user.getId());
                return new UserResponseWithMainPhoto(
                        user,
                        currentUserPhoto.orElse(null)
                );
            }).toList();

        } catch (Exception e) {
            applicationServiceLogger.error("Search users by username failed", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public UserResponseWithAllPhotos getYourUserData() {
        try {
            applicationServiceLogger.info("Get your user data started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User user = jpaUserDomainService.getById(
                    UUID.fromString(authInfo.getUserId())
            );

            List<UserProfilePhoto> userProfilePhotos = jpaUserProfilePhotoDomainService.getAllByUserId(
                    user.getId()
            );

            applicationServiceLogger.info("Get your user data finished");

            return new UserResponseWithAllPhotos(user, userProfilePhotos);

        } catch (Exception e) {
            applicationServiceLogger.error("Get your user data failed", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public UserResponseWithAllPhotos getUserById(@NotNull String userId) {
        try {
            applicationServiceLogger.info("Get user by id started");

            jwtDomainService.getAuthInfo();
            UUID uuidUserId = UUID.fromString(userId);

            User user = jpaUserDomainService.getById(uuidUserId);
            List<UserProfilePhoto> userProfilePhotos = jpaUserProfilePhotoDomainService.getAllByUserId(uuidUserId);

            applicationServiceLogger.info("Get user by id finished");

            return new UserResponseWithAllPhotos(user, userProfilePhotos);

        } catch (Exception e) {
            applicationServiceLogger.error("Get user by id failed", e);
            throw e;
        }
    }


    @Transactional
    public HttpResponse deleteAccount() {
        try {
            applicationServiceLogger.info("Delete user account started");

            JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
            User postgresUser = jpaUserDomainService.getById(
                    UUID.fromString(authInfo.getUserId())
            );

            jpaUserProfilePhotoDomainService.getAllByUserId(postgresUser.getId())
                    .forEach(photo -> minioUserProfilePhotoDomainService.delete(photo.getFilePath()));

            jpaUserDomainService.deleteById(postgresUser.getId());

            redisSessionDomainService.deleteByUserId(postgresUser.getId());

            applicationServiceLogger.info("Delete user account finished");

            return new HttpResponse("User deleted successfully", 200);

        } catch (Exception e) {
            applicationServiceLogger.error("Delete user account failed", e);
            throw e;
        }
    }
}