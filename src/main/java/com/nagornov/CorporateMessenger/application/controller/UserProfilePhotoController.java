package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.UserProfilePhotoApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserProfilePhotoController {

    private final UserProfilePhotoApplicationService userProfilePhotoApplicationService;

    @PostMapping(
            value = "/api/user/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserProfilePhoto> loadUserProfilePhoto(@Validated @ModelAttribute FileRequest request) {
        final UserProfilePhoto response =
                userProfilePhotoApplicationService.loadUserProfilePhoto(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/user/{userId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getMainUserProfilePhotoByUserId(@ValidUuid @PathVariable("userId") String userId) {
        final Resource response =
                userProfilePhotoApplicationService.getMainUserProfilePhotoByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getUserProfilePhotoByPhotoId(@ValidUuid @PathVariable("photoId") String photoId) {
        final Resource response =
                userProfilePhotoApplicationService.getUserProfilePhotoByPhotoId(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserProfilePhoto> setMainUserProfilePhoto(@ValidUuid @PathVariable("photoId") String photoId) {
        final UserProfilePhoto response =
                userProfilePhotoApplicationService.setMainUserProfilePhoto(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteUserProfilePhoto(@ValidUuid @PathVariable("photoId") String photoId) {
        final HttpResponse response =
                userProfilePhotoApplicationService.deleteUserProfilePhoto(photoId);
        return ResponseEntity.status(200).body(response);
    }
}