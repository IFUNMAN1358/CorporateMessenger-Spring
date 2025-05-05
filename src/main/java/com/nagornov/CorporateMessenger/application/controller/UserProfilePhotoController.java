package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.UserPhotoApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class UserProfilePhotoController {

    private final UserPhotoApplicationService userPhotoApplicationService;

    @PostMapping(
            value = "/api/user/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserPhoto> loadUserPhoto(@ModelAttribute FileRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("FileRequest validation error", bindingResult);
        }
        UserPhoto response = userPhotoApplicationService.loadUserPhoto(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/user/{userId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getMainUserPhotoByUserId(@PathVariable UUID userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(userId) validation error", bindingResult);
        }
        Resource response = userPhotoApplicationService.getMainUserPhotoByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getUserPhotoByPhotoId(@PathVariable UUID photoId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(photoId) validation error", bindingResult);
        }
        Resource response = userPhotoApplicationService.getUserPhotoByPhotoId(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserPhoto> setMainUserPhoto(@PathVariable UUID photoId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(photoId) validation error", bindingResult);
        }
        UserPhoto response = userPhotoApplicationService.setMainUserPhoto(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            value = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteUserPhoto(@PathVariable UUID photoId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(photoId) validation error", bindingResult);
        }
        HttpResponse response = userPhotoApplicationService.deleteUserPhoto(photoId);
        return ResponseEntity.status(200).body(response);
    }
}