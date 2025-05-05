package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.AuthApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;


    @GetMapping(
            path = "/api/auth/csrf-token",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        HttpResponse res = authApplicationService.getCsrfToken(request, response);
        return ResponseEntity.status(200).body(res);
    }


    @PostMapping(
            value = "/api/auth/registration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> registration(@RequestBody RegistrationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("RegistrationRequest validation error", bindingResult);
        }
        JwtResponse response = authApplicationService.registration(request);
        return ResponseEntity.status(201).body(response);
    }


    @PostMapping(
            value = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("LoginRequest validation error", bindingResult);
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
