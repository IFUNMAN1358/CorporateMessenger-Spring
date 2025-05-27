package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.model.user.*;
import com.nagornov.CorporateMessenger.application.applicationService.UserApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;


    @PatchMapping(
            path = "/api/user/firstName-and-lastName",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserFirstNameAndLastName(
            @RequestBody UserFirstNameAndLastNameRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserFirstNameAndLastNameRequest validation error", bindingResult);
        }
        userApplicationService.changeUserFirstNameAndLastName(request);
        return ResponseEntity.status(204).body("FirstName and lastName updated");
    }


    @PatchMapping(
            path = "/api/user/username",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserUsername(
            @Valid @RequestBody UserUsernameRequest request,
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
            path = "/api/user/bio",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserBio(
            @RequestBody UserBioRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserBioRequest validation error", bindingResult);
        }
        userApplicationService.changeUserBio(request);
        return ResponseEntity.status(204).body("Bio updated");
    }


    @PatchMapping(
            path = "/api/user/main-email",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeUserMainEmail() {
        userApplicationService.changeUserMainEmail();
        return ResponseEntity.status(204).body("Main email updated");
    }


    @PatchMapping(
            path = "/api/user/phone",
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
            path = "/api/user/exists-username",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Boolean> existsUserByUsername(
            @RequestParam("username") String username
    ) {
        Boolean response = userApplicationService.existsByUsername(username);
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


    @GetMapping(
            path = "/api/user/blocked",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserWithUserPhotoResponse>> findAllMyBlockedUsers(
            @RequestParam int page,
            @RequestParam int size
    ) {
        List<UserWithUserPhotoResponse> response = userApplicationService.findAllMyBlockedUsers(page, size);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/{userId}/block",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> blockUserByUserId(
            @PathVariable UUID userId
    ) {
        userApplicationService.blockUserByUserId(userId);
        return ResponseEntity.status(200).body("User blocked");
    }


    @PatchMapping(
            path = "/api/user/{userId}/unblock",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> unblockUserByUserId(
            @PathVariable UUID userId
    ) {
        userApplicationService.unblockUserByUserId(userId);
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