package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.application.applicationService.UserDataApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserDataApplicationService userDataApplicationService;

    @PatchMapping(
            value = "/api/user/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> changeUserPassword(@Validated @RequestBody PasswordRequest request) {
        HttpResponse response =
                userDataApplicationService.changeUserPassword(request);
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
                userDataApplicationService.searchUsersByUsername(username, page, 10);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getYourUserData() {
        UserResponseWithAllPhotos response =
                userDataApplicationService.getYourUserData();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getUserById(@PathVariable("userId") String userId) {
        UserResponseWithAllPhotos response =
                userDataApplicationService.getUserById(userId);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteAccount() {
        HttpResponse response =
                userDataApplicationService.deleteAccount();
        return ResponseEntity.status(200).body(response);
    }
}