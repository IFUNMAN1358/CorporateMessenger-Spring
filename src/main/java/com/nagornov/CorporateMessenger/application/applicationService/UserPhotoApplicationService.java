package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPhotoApplicationService {

    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserPhotoDomainService jpaUserPhotoDomainService;
    private final MinioUserPhotoDomainService minioUserPhotoDomainService;
    private final JwtDomainService jwtDomainService;


    @Transactional
    public UserPhoto loadUserPhoto(FileRequest request) {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(
                authInfo.getUserIdAsUUID()
        );

        MultipartFile file = request.getFile();
        String filePath = minioUserPhotoDomainService.upload(file);

        UserPhoto userPhoto = new UserPhoto(
                UUID.randomUUID(),
                postgresUser.getId(),
                file.getOriginalFilename(),
                filePath,
                file.getContentType(),
                true,
                LocalDateTime.now()
        );
        jpaUserPhotoDomainService.save(userPhoto);

        Optional<UserPhoto> currentUserPhoto = jpaUserPhotoDomainService.findMainByUserId(postgresUser.getId());
        currentUserPhoto.ifPresent(UserPhoto::unmarkAsMain);
        currentUserPhoto.ifPresent(jpaUserPhotoDomainService::save);

        return jpaUserPhotoDomainService.getById(userPhoto.getId());
    }


    @Transactional(readOnly = true)
    public Resource getMainUserPhotoByUserId(String userId) {

        jwtDomainService.getAuthInfo();

        Optional<UserPhoto> userPhoto = jpaUserPhotoDomainService.findMainByUserId(
                UUID.fromString(userId)
        );

        if (userPhoto.isEmpty()) {
            throw new ResourceNotFoundException("User's main photo not found");
        }

        return new InputStreamResource(
                minioUserPhotoDomainService.download(userPhoto.get().getFilePath())
        );
    }


    @Transactional(readOnly = true)
    public Resource getUserPhotoByPhotoId(String photoId) {

        jwtDomainService.getAuthInfo();

        UserPhoto userPhoto = jpaUserPhotoDomainService.getById(
                UUID.fromString(photoId)
        );

        return new InputStreamResource(
                minioUserPhotoDomainService.download(userPhoto.getFilePath())
        );
    }


    @Transactional
    public UserPhoto setMainUserPhoto(String photoId) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(
                authInfo.getUserIdAsUUID()
        );

        UserPhoto newMainPhoto = jpaUserPhotoDomainService.getByIdAndUserId(
                UUID.fromString(photoId),
                postgresUser.getId()
        );
        newMainPhoto.markAsMain();

        Optional<UserPhoto> currentUserPhoto =
                jpaUserPhotoDomainService.findMainByUserId(postgresUser.getId());
        currentUserPhoto.ifPresent(UserPhoto::unmarkAsMain);

        currentUserPhoto.ifPresent(jpaUserPhotoDomainService::save);
        jpaUserPhotoDomainService.save(newMainPhoto);

        return jpaUserPhotoDomainService.getById(newMainPhoto.getId());
    }


    @Transactional
    public HttpResponse deleteUserPhoto(String photoId) {
        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();
        User postgresUser = jpaUserDomainService.getById(
                authInfo.getUserIdAsUUID()
        );

        UserPhoto photoToDelete = jpaUserPhotoDomainService.getByIdAndUserId(
                UUID.fromString(photoId),
                postgresUser.getId()
        );

        if (photoToDelete.getIsMain()) {
            List<UserPhoto> userPhotos =
                    jpaUserPhotoDomainService.getAllByUserId(postgresUser.getId())
                            .stream().filter(
                                    photo -> !photo.getId().equals(photoToDelete.getId())
                            ).toList();
            if (!userPhotos.isEmpty()) {
                UserPhoto newMainPhoto = userPhotos.getFirst();
                newMainPhoto.markAsMain();
                jpaUserPhotoDomainService.save(newMainPhoto);
            }
        }

        jpaUserPhotoDomainService.deleteById(photoToDelete.getId());
        minioUserPhotoDomainService.delete(photoToDelete.getFilePath());

        return new HttpResponse("User photo deleted successfully", 200);
    }
}