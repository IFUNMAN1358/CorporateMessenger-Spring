package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.model.user.*;
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

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;


    @PatchMapping(
            path = "/api/user/username",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserUsername(
            @RequestBody UserUsernameRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UsernameRequest validation error", bindingResult);
        }
        userApplicationService.changeUserUsername(request);
        return ResponseEntity.status(204).body("Username updated");
    }


    @PatchMapping(
            path = "/api/user/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserPassword(
            @RequestBody UserPasswordRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PasswordRequest validation error", bindingResult);
        }
        userApplicationService.changeUserPassword(request);
        return ResponseEntity.status(204).body("Password updated");
    }


    @PatchMapping(
            path = "/api/user/main-email",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserMainEmail() {
        userApplicationService.changeUserMainEmail();
        return ResponseEntity.status(204).body("Main email updated");
    }


    @PatchMapping(
            path = "/api/user/phone",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserPhone() {
        userApplicationService.changeUserPhone();
        return ResponseEntity.status(204).body("Phone updated");
    }


    @GetMapping(
            path = "/api/user/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Page<UserWithUserPhotoResponse>> searchUsersByUsername(
            @RequestParam("username") String username,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        Page<UserWithUserPhotoResponse> response = userApplicationService.searchUsersByUsername(username, page, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserWithUserPhotosResponse> getMyUserData() {
        UserWithUserPhotosResponse response = userApplicationService.getMyUserData();
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserWithUserPhotosResponse> getUserByUserId(
            @PathVariable("userId") UUID userId
    ) {
        UserWithUserPhotosResponse response = userApplicationService.getUserByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/block",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> blockUserByUserId(
            @RequestBody UserIdRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest validation error", bindingResult);
        }
        userApplicationService.blockUserByUserId(request);
        return ResponseEntity.status(200).body("User blocked");
    }


    @PatchMapping(
            path = "/api/user/unblock",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> unblockUserByUserId(
            @RequestBody UserIdRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest validation error", bindingResult);
        }
        userApplicationService.unblockUserByUserId(request);
        return ResponseEntity.status(200).body("User unlocked");
    }


    @DeleteMapping(
            path = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> softDeleteAccount() {
        userApplicationService.softDeleteAccount();
        return ResponseEntity.status(200).body("Account deleted");
    }
}