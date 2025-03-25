package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.AuthApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping(
            value = "/api/auth/registration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> registration(
            @Validated @RequestBody RegistrationRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException(bindingResult);
        }

        JwtResponse response = authApplicationService.registration(request);

        return ResponseEntity.status(201).body(response);
    }


    @PostMapping(
            value = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> login(
            @Validated @RequestBody LoginRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException(bindingResult);
        }

        JwtResponse response = authApplicationService.login(request);

        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            value = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> logout() {
        HttpResponse response = authApplicationService.logout();
        return ResponseEntity.status(200).body(response);
    }
}
