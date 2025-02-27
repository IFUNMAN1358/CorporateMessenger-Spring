package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.UserProfilePhotoApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
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
    private final ControllerLogger controllerLogger;

    @PostMapping(
            value = "/api/user/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserProfilePhoto> loadUserProfilePhoto(@Validated @ModelAttribute FileRequest request) {
        try {
            controllerLogger.info("Load user profile photo started");
            final UserProfilePhoto response =
                    userProfilePhotoApplicationService.loadUserProfilePhoto(request);
            controllerLogger.info("Load user profile photo finished");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            controllerLogger.error("Load user profile photo failed", e);
            throw e;
        }
    }


    @GetMapping(
            value = "/api/user/{userId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getMainUserProfilePhotoByUserId(@ValidUuid @PathVariable("userId") String userId) {
        try {
            controllerLogger.info("Get main user profile photo started");
            final Resource response =
                    userProfilePhotoApplicationService.getMainUserProfilePhotoByUserId(userId);
            controllerLogger.info("Get main user profile photo finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get main user profile photo failed", e);
            throw e;
        }
    }


    @GetMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getUserProfilePhotoByPhotoId(@ValidUuid @PathVariable("photoId") String photoId) {
        try {
            controllerLogger.info("Get user profile photo started");
            final Resource response =
                    userProfilePhotoApplicationService.getUserProfilePhotoByPhotoId(photoId);
            controllerLogger.info("Get user profile photo finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get user profile photo failed", e);
            throw e;
        }
    }


    @PatchMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserProfilePhoto> setMainUserProfilePhoto(@ValidUuid @PathVariable("photoId") String photoId) {
        try {
            controllerLogger.info("Set main user profile photo started");
            final UserProfilePhoto response =
                    userProfilePhotoApplicationService.setMainUserProfilePhoto(photoId);
            controllerLogger.info("Set main user profile photo finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Set main user profile photo failed", e);
            throw e;
        }
    }


    @DeleteMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteUserProfilePhoto(@ValidUuid @PathVariable("photoId") String photoId) {
        try {
            controllerLogger.info("Delete user profile photo started");
            final HttpResponse response =
                    userProfilePhotoApplicationService.deleteUserProfilePhoto(photoId);
            controllerLogger.info("Delete user profile photo finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Delete user profile photo failed", e);
            throw e;
        }
    }
}