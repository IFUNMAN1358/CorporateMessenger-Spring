package com.nagornov.CorporateMessenger.auth.application.controller;

import com.nagornov.CorporateMessenger.auth.application.dto.request.LoginFormRequest;
import com.nagornov.CorporateMessenger.auth.application.dto.request.RegistrationFormRequest;
import com.nagornov.CorporateMessenger.auth.application.dto.response.JwtResponse;
import com.nagornov.CorporateMessenger.auth.application.service.ApplicationLoginService;
import com.nagornov.CorporateMessenger.auth.application.service.ApplicationLogoutService;
import com.nagornov.CorporateMessenger.auth.application.service.ApplicationRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final ApplicationRegistrationService applicationRegistrationService;
    private final ApplicationLoginService applicationLoginService;
    private final ApplicationLogoutService applicationLogoutService;


    @PostMapping(
        value = "/api/auth/registration",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> registration(@Valid @RequestBody final RegistrationFormRequest request) {
        final JwtResponse response =
                applicationRegistrationService.registration(request);
        return ResponseEntity.status(201).body(response);
    }


    @PostMapping(
        value = "/api/auth/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> login(@Valid @RequestBody final LoginFormRequest request) {
        final JwtResponse response =
                applicationLoginService.login(request);
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
        value = "/api/auth/logout",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> logout() {
        applicationLogoutService.logout();
        return ResponseEntity.status(200).body("Successfully logged out");
    }
}
