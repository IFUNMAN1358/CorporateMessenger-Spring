package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.UserService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisJwtSessionDomainService;
import com.nagornov.CorporateMessenger.domain.service.JwtService;
import com.nagornov.CorporateMessenger.domain.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final JpaUserPhotoDomainService jpaUserPhotoDomainService;
    private final MinioUserPhotoDomainService minioUserPhotoDomainService;
    private final RedisJwtSessionDomainService redisJwtSessionDomainService;
    private final PasswordService passwordService;


    @Transactional
    public HttpResponse changeUserPassword(PasswordRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        passwordService.matchEncodedPassword(request.getCurrentPassword(), postgresUser.getPassword());

        String encodedPassword = passwordService.encodePassword(request.getNewPassword());
        postgresUser.updatePassword(encodedPassword);
        userService.save(postgresUser);

        return new HttpResponse("Password changed", 200);
    }


    @Transactional(readOnly = true)
    public List<UserResponseWithMainPhoto> searchUsersByUsername(String username, int page, int pageSize) {

        jwtService.getAuthInfo();

        List<User> userList = userService.searchByUsername(username, page, pageSize);

        return userList.stream().map(user -> {
            Optional<UserPhoto> currentUserPhoto = jpaUserPhotoDomainService.findMainByUserId(user.getId());
            return new UserResponseWithMainPhoto(
                    user,
                    currentUserPhoto.orElse(null)
            );
        }).toList();
    }


    @Transactional(readOnly = true)
    public UserResponseWithAllPhotos getYourUserData() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        List<UserPhoto> userPhotos = jpaUserPhotoDomainService.getAllByUserId(
                user.getId()
        );

        return new UserResponseWithAllPhotos(user, userPhotos);
    }


    @Transactional(readOnly = true)
    public UserResponseWithAllPhotos getUserById(String userId) {

        jwtService.getAuthInfo();
        UUID uuidUserId = UUID.fromString(userId);

        User user = userService.getById(uuidUserId);
        List<UserPhoto> userPhotos = jpaUserPhotoDomainService.getAllByUserId(uuidUserId);

        return new UserResponseWithAllPhotos(user, userPhotos);
    }


    @Transactional
    public HttpResponse deleteAccount() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        jpaUserPhotoDomainService.getAllByUserId(postgresUser.getId())
                .forEach(photo -> minioUserPhotoDomainService.delete(photo.getFilePath()));

        userService.deleteById(postgresUser.getId());

        redisJwtSessionDomainService.deleteByKey(postgresUser.getId());

        return new HttpResponse("User deleted successfully", 200);
    }
}