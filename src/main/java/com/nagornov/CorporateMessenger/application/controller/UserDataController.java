package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.PasswordRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithAllPhotos;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponseWithMainPhoto;
import com.nagornov.CorporateMessenger.application.applicationService.UserDataApplicationService;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
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
    private final ControllerLogger controllerLogger;

    @PatchMapping(
            value = "/api/user/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> changeUserPassword(@Validated @RequestBody PasswordRequest request) {
        try {
            controllerLogger.info("Change user password started");
            final HttpResponse response =
                    userDataApplicationService.changeUserPassword(request);
            controllerLogger.info("Change user password finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Change user password failed", e);
            throw e;
        }
    }

    @GetMapping(
            value = "/api/user/search/{username}/{page}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserResponseWithMainPhoto>> searchUsersByUsername(
            @PathVariable("username") String username, @PathVariable("page") int page
    ) {
        try {
            controllerLogger.info("Search users by username started");
            final List<UserResponseWithMainPhoto> response =
                    userDataApplicationService.searchUsersByUsername(username, page, 10);
            controllerLogger.info("Search users by username finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Search users by username failed", e);
            throw e;
        }
    }

    @GetMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getYourUserData() {
        try {
            controllerLogger.info("Get user data started");
            final UserResponseWithAllPhotos response =
                    userDataApplicationService.getYourUserData();
            controllerLogger.info("Get user data finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get user data failed", e);
            throw e;
        }
    }

    @GetMapping(
            value = "/api/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserResponseWithAllPhotos> getUserById(@PathVariable("userId") String userId) {
        try {
            controllerLogger.info("Get user by id started");
            final UserResponseWithAllPhotos response =
                    userDataApplicationService.getUserById(userId);
            controllerLogger.info("Get user by id finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get user by id failed", e);
            throw e;
        }
    }

    @DeleteMapping(
            value = "/api/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteAccount() {
        try {
            controllerLogger.info("Delete user started");
            final HttpResponse response =
                    userDataApplicationService.deleteAccount();
            controllerLogger.info("Delete user finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Delete user failed", e);
            throw e;
        }
    }
}