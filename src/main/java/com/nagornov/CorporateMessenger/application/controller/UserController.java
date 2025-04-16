package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.application.applicationService.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PatchMapping(
            value = "/api/user/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> changeUserPassword(@Validated @RequestBody PasswordRequest request) {
        HttpResponse response =
                userApplicationService.changeUserPassword(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserResponseWithMainPhoto>> searchUsersByUsername(
            @RequestParam("username") String username,
            @RequestParam("page") int page
    ) {
        List<UserResponseWithMainPhoto> response =
                userApplicationService.searchUsersByUsername(username, page, 10);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getYourUserData() {
        UserResponseWithAllPhotos response =
                userApplicationService.getYourUserData();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getUserById(@PathVariable("userId") String userId) {
        UserResponseWithAllPhotos response =
                userApplicationService.getUserById(userId);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteAccount() {
        HttpResponse response =
                userApplicationService.deleteAccount();
        return ResponseEntity.status(200).body(response);
    }
}