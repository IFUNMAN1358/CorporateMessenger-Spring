package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.UserPhotoApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserProfilePhotoController {

    private final UserPhotoApplicationService userPhotoApplicationService;

    @PostMapping(
            value = "/api/user/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserPhoto> loadUserPhoto(@Validated @ModelAttribute FileRequest request) {
        UserPhoto response =
                userPhotoApplicationService.loadUserPhoto(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/user/{userId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getMainUserPhotoByUserId(@ValidUuid @PathVariable("userId") String userId) {
        Resource response =
                userPhotoApplicationService.getMainUserPhotoByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getUserPhotoByPhotoId(@ValidUuid @PathVariable("photoId") String photoId) {
        Resource response =
                userPhotoApplicationService.getUserPhotoByPhotoId(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserPhoto> setMainUserPhoto(@ValidUuid @PathVariable("photoId") String photoId) {
        UserPhoto response =
                userPhotoApplicationService.setMainUserPhoto(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteUserPhoto(@ValidUuid @PathVariable("photoId") String photoId) {
        HttpResponse response =
                userPhotoApplicationService.deleteUserPhoto(photoId);
        return ResponseEntity.status(200).body(response);
    }
}