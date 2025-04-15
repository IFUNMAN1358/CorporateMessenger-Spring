package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisSessionDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.PasswordDomainService;
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
    private final JpaUserPhotoDomainService jpaUserPhotoDomainService;
    private final MinioUserPhotoDomainService minioUserPhotoDomainService;
    private final RedisSessionDomainService redisSessionDomainService;
    private final PasswordDomainService passwordDomainService;


    @Transactional
    public HttpResponse changeUserPassword(PasswordRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        passwordDomainService.matchEncodedPassword(request.getCurrentPassword(), postgresUser.getPassword());

        String encodedPassword = passwordDomainService.encodePassword(request.getNewPassword());
        postgresUser.updatePassword(encodedPassword);
        jpaUserDomainService.save(postgresUser);

        return new HttpResponse("Password changed", 200);
    }


    @Transactional(readOnly = true)
    public List<UserResponseWithMainPhoto> searchUsersByUsername(String username, int page, int pageSize) {

        jwtDomainService.getAuthInfo();

        List<User> userList = jpaUserDomainService.searchByUsername(username, page, pageSize);

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

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User user = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        List<UserPhoto> userPhotos = jpaUserPhotoDomainService.getAllByUserId(
                user.getId()
        );

        return new UserResponseWithAllPhotos(user, userPhotos);
    }


    @Transactional(readOnly = true)
    public UserResponseWithAllPhotos getUserById(String userId) {

        jwtDomainService.getAuthInfo();
        UUID uuidUserId = UUID.fromString(userId);

        User user = jpaUserDomainService.getById(uuidUserId);
        List<UserPhoto> userPhotos = jpaUserPhotoDomainService.getAllByUserId(uuidUserId);

        return new UserResponseWithAllPhotos(user, userPhotos);
    }


    @Transactional
    public HttpResponse deleteAccount() {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        jpaUserPhotoDomainService.getAllByUserId(postgresUser.getId())
                .forEach(photo -> minioUserPhotoDomainService.delete(photo.getFilePath()));

        jpaUserDomainService.deleteById(postgresUser.getId());

        redisSessionDomainService.deleteByUserId(postgresUser.getId());

        return new HttpResponse("User deleted successfully", 200);
    }
}