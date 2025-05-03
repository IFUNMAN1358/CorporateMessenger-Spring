package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.*;
import com.nagornov.CorporateMessenger.application.applicationService.UserApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;


    @PatchMapping(
            value = "/api/user/username",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserUsername(@RequestBody UsernameRequest request) {
        userApplicationService.changeUserUsername(request);
        return ResponseEntity.status(204).body("Username updated");
    }


    @PatchMapping(
            value = "/api/user/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserPassword(@Validated @RequestBody PasswordRequest request) {
        userApplicationService.changeUserPassword(request);
        return ResponseEntity.status(204).body("Password updated");
    }


    @PatchMapping(
            value = "/api/user/main-email",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserMainEmail() {
        userApplicationService.changeUserMainEmail();
        return ResponseEntity.status(204).body("Main email updated");
    }


    @PatchMapping(
            value = "/api/user/phone",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserPhone() {
        userApplicationService.changeUserPhone();
        return ResponseEntity.status(204).body("Phone updated");
    }


    @GetMapping(
            value = "/api/user/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Page<UserWithMainPhotoResponse>> searchUsersByUsername(
            @RequestParam("username") String username,
            @RequestParam("page") int page
    ) {
        Page<UserWithMainPhotoResponse> response = userApplicationService.searchUsersByUsername(username, page, 10);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserWithPhotosResponse> getYourUserData() {
        UserWithPhotosResponse response = userApplicationService.getYourUserData();
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserWithPhotosResponse> getUserById(@PathVariable("userId") UUID userId) {
        UserWithPhotosResponse response = userApplicationService.getUserById(userId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/block",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> blockUserByUserId(@Validated @RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest form validation error", bindingResult);
        }
        userApplicationService.blockUserByUserId(request);
        return ResponseEntity.status(200).body("User blocked");
    }


    @PatchMapping(
            path = "/api/user/unblock",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> unblockUserByUserId(@Validated @RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest form validation error", bindingResult);
        }
        userApplicationService.unblockUserByUserId(request);
        return ResponseEntity.status(200).body("User unlocked");
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