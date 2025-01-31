package com.nagornov.CorporateMessenger.application.service;

import com.nagornov.CorporateMessenger.application.dto.common.InformationalResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
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


    @Transactional
    public InformationalResponse changePassword(@NotNull PasswordRequest request) {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        passwordDomainService.matchPassword(request.getCurrentPassword(), postgresUser.getPassword());

        final String encodedPassword = passwordDomainService.encodePassword(request.getNewPassword());
        postgresUser.updatePassword(encodedPassword);
        jpaUserDomainService.update(postgresUser);

        return new InformationalResponse("Password changed");
    }


    @Transactional(readOnly = true)
    public List<UserResponseWithMainPhoto> searchByUsername(@NotNull String username, @NotNull int page, @NotNull int pageSize) {
        jwtDomainService.getAuthInfo();

        final List<User> userList = jpaUserDomainService.searchByUsername(username, page, pageSize);

        return userList.stream().map(user -> {
            Optional<UserProfilePhoto> currentUserPhoto = jpaUserProfilePhotoDomainService.findMainByUserId(user.getId());
            return new UserResponseWithMainPhoto(
                    user,
                    currentUserPhoto.orElse(null)
            );
        }).toList();
    }


    @Transactional(readOnly = true)
    public UserResponseWithAllPhotos getProfile() {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User user = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );
        final List<UserProfilePhoto> userProfilePhotos = jpaUserProfilePhotoDomainService.getAllByUserId(
                user.getId()
        );

        return new UserResponseWithAllPhotos(user, userProfilePhotos);
    }


    @Transactional(readOnly = true)
    public UserResponseWithAllPhotos getUser(@NotNull String userId) {
        jwtDomainService.getAuthInfo();

        final UUID uuidUserId = UUID.fromString(userId);

        final User user = jpaUserDomainService.getById(uuidUserId);
        final List<UserProfilePhoto> userProfilePhotos = jpaUserProfilePhotoDomainService.getAllByUserId(uuidUserId);

        return new UserResponseWithAllPhotos(user, userProfilePhotos);
    }


    @Transactional
    public InformationalResponse deleteUser() {
        final JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        final User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        jpaUserProfilePhotoDomainService.getAllByUserId(postgresUser.getId())
                .forEach(photo -> minioUserProfilePhotoDomainService.delete(photo.getFilePath()));

        jpaUserDomainService.deleteById(postgresUser.getId());

        redisSessionDomainService.deleteByUserId(postgresUser.getId());

        return new InformationalResponse("User deleted successfully");
    }
}