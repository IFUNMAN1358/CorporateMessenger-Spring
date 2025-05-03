package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.domain.service.user.UserPhotoService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import com.nagornov.CorporateMessenger.domain.service.domainService.minio.MinioUserPhotoDomainService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
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

    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private final MinioUserPhotoDomainService minioUserPhotoDomainService;
    private final JwtService jwtService;


    @Transactional
    public UserPhoto loadUserPhoto(FileRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(
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
        userPhotoService.save(userPhoto);

        Optional<UserPhoto> currentUserPhoto = userPhotoService.findMainByUserId(postgresUser.getId());
        currentUserPhoto.ifPresent(UserPhoto::unmarkAsMain);
        currentUserPhoto.ifPresent(userPhotoService::save);

        return userPhotoService.getById(userPhoto.getId());
    }


    @Transactional(readOnly = true)
    public Resource getMainUserPhotoByUserId(String userId) {

        jwtService.getAuthInfo();

        Optional<UserPhoto> userPhoto = userPhotoService.findMainByUserId(
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

        jwtService.getAuthInfo();

        UserPhoto userPhoto = userPhotoService.getById(
                UUID.fromString(photoId)
        );

        return new InputStreamResource(
                minioUserPhotoDomainService.download(userPhoto.getFilePath())
        );
    }


    @Transactional
    public UserPhoto setMainUserPhoto(String photoId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(
                authInfo.getUserIdAsUUID()
        );

        UserPhoto newMainPhoto = userPhotoService.getByIdAndUserId(
                UUID.fromString(photoId),
                postgresUser.getId()
        );
        newMainPhoto.markAsMain();

        Optional<UserPhoto> currentUserPhoto =
                userPhotoService.findMainByUserId(postgresUser.getId());
        currentUserPhoto.ifPresent(UserPhoto::unmarkAsMain);

        currentUserPhoto.ifPresent(userPhotoService::save);
        userPhotoService.save(newMainPhoto);

        return userPhotoService.getById(newMainPhoto.getId());
    }


    @Transactional
    public HttpResponse deleteUserPhoto(String photoId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User postgresUser = userService.getById(
                authInfo.getUserIdAsUUID()
        );

        UserPhoto photoToDelete = userPhotoService.getByIdAndUserId(
                UUID.fromString(photoId),
                postgresUser.getId()
        );

        if (photoToDelete.getIsMain()) {
            List<UserPhoto> userPhotos =
                    userPhotoService.getAllByUserId(postgresUser.getId())
                            .stream().filter(
                                    photo -> !photo.getId().equals(photoToDelete.getId())
                            ).toList();
            if (!userPhotos.isEmpty()) {
                UserPhoto newMainPhoto = userPhotos.getFirst();
                newMainPhoto.markAsMain();
                userPhotoService.save(newMainPhoto);
            }
        }

        userPhotoService.deleteById(photoToDelete.getId());
        minioUserPhotoDomainService.delete(photoToDelete.getFilePath());

        return new HttpResponse("User photo deleted successfully", 200);
    }
}