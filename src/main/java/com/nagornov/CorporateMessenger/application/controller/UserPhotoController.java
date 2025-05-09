package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.applicationService.UserPhotoApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserPhotoResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UserPhotoController {

    private final UserPhotoApplicationService userPhotoApplicationService;


    @PostMapping(
            path = "/api/user/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserPhotoResponse> loadMyUserPhoto(
            @ModelAttribute FileRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("FileRequest validation error", bindingResult);
        }
        UserPhotoResponse response = userPhotoApplicationService.loadMyUserPhoto(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/user/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMyMainUserPhoto(
            @NotBlank @RequestParam String size, // big / small
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("RequestParam(size) validation error", bindingResult);
        }
        Resource response = userPhotoApplicationService.downloadMyMainUserPhoto(size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMyUserPhotoByPhotoId(
            @NotNull @PathVariable UUID photoId,
            @NotBlank @RequestParam String size, // big / small
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(photoId) | RequestParam(size) validation error", bindingResult);
        }
        Resource response = userPhotoApplicationService.downloadMyUserPhotoByPhotoId(photoId, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMainUserPhotoByUserId(
            @NotNull @PathVariable UUID userId,
            @NotBlank @RequestParam String size, // big / small
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(userId) | RequestParam(size) validation error", bindingResult);
        }
        Resource response = userPhotoApplicationService.downloadMainUserPhotoByUserId(userId, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadUserPhotoByUserIdAndPhotoId(
            @NotNull @PathVariable UUID userId,
            @NotNull @PathVariable UUID photoId,
            @NotBlank @RequestParam String size, // big / small
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(userId) | PathVariable(photoId) | RequestParam(size) validation error", bindingResult);
        }
        Resource response = userPhotoApplicationService.downloadUserPhotoByUserIdAndPhotoId(userId, photoId, size);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserPhotoResponse> setMyMainUserPhoto(
            @NotNull @PathVariable UUID photoId,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(photoId) validation error", bindingResult);
        }
        UserPhotoResponse response = userPhotoApplicationService.setMyMainUserPhoto(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            path = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteMyUserPhoto(
            @NotNull @PathVariable UUID photoId,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(photoId) validation error", bindingResult);
        }
        userPhotoApplicationService.deleteMyUserPhoto(photoId);
        return ResponseEntity.status(200).body("User photo deleted");
    }
}