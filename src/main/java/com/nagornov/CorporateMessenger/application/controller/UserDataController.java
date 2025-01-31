package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.InformationalResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.application.service.UserDataApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserDataController {

    private final UserDataApplicationService userDataApplicationService;

    @PatchMapping(
            value = "/api/user/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> changePassword(@Validated @RequestBody PasswordRequest request) {
        final InformationalResponse response =
                userDataApplicationService.changePassword(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user/search/{username}/{page}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserResponseWithMainPhoto>> searchByUsername(
            @PathVariable("username") String username, @PathVariable("page") int page
    ) {
        final List<UserResponseWithMainPhoto> response =
                userDataApplicationService.searchByUsername(username, page, 10);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getProfile() {
        final UserResponseWithAllPhotos response = userDataApplicationService.getProfile();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getUser(@PathVariable("userId") String userId) {
        final UserResponseWithAllPhotos response =
                userDataApplicationService.getUser(userId);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> deleteUser() {
        final InformationalResponse response =
                userDataApplicationService.deleteUser();
        return ResponseEntity.status(200).body(response);
    }
}