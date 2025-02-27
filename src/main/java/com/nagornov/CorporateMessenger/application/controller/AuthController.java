package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.AuthApplicationService;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final ControllerLogger controllerLogger;

    @PostMapping(
            value = "/api/auth/registration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> registration(@Validated @RequestBody RegistrationRequest request) {
        try {
            controllerLogger.info("Registration started");
            final JwtResponse response =
                    authApplicationService.registration(request);
            controllerLogger.info("Registration finished");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            controllerLogger.error("Registration failed", e);
            throw e;
        }
    }


    @PostMapping(
            value = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> login(@Validated @RequestBody LoginRequest request) {
        try {
            controllerLogger.info("Login started");
            final JwtResponse response =
                    authApplicationService.login(request);
            controllerLogger.info("Login finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Login failed", e);
            throw e;
        }
    }


    @PostMapping(
            value = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> logout() {
        try {
            controllerLogger.info("Logout started");
            final HttpResponse response =
                    authApplicationService.logout();
            controllerLogger.info("Logout finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Logout failed", e);
            throw e;
        }
    }
}
