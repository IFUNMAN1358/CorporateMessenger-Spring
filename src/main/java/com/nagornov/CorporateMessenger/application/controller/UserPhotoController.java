package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.applicationService.UserPhotoApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserPhotoResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
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
    ResponseEntity<UserPhotoResponse> uploadMyUserPhoto(
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
            @RequestParam String size // big / small
    ) {
        Resource response = userPhotoApplicationService.downloadMyMainUserPhoto(size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMyUserPhotoByPhotoId(
            @PathVariable UUID photoId,
            @RequestParam String size // big / small
    ) {
        Resource response = userPhotoApplicationService.downloadMyUserPhotoByPhotoId(photoId, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMainUserPhotoByUserId(
            @PathVariable UUID userId,
            @RequestParam String size // big / small
    ) {
        Resource response = userPhotoApplicationService.downloadMainUserPhotoByUserId(userId, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadUserPhotoByUserIdAndPhotoId(
            @PathVariable UUID userId,
            @PathVariable UUID photoId,
            @RequestParam String size // big / small
    ) {
        Resource response = userPhotoApplicationService.downloadUserPhotoByUserIdAndPhotoId(userId, photoId, size);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserPhotoResponse> setMyMainUserPhotoByPhotoId(
            @PathVariable UUID photoId
    ) {
        UserPhotoResponse response = userPhotoApplicationService.setMyMainUserPhotoByPhotoId(photoId);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            path = "/api/user/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteMyUserPhotoByPhotoId(
            @PathVariable UUID photoId
    ) {
        userPhotoApplicationService.deleteMyUserPhotoByPhotoId(photoId);
        return ResponseEntity.status(200).body("User photo deleted");
    }
}