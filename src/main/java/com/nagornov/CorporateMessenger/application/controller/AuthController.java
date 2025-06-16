package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.SessionResponse;
import com.nagornov.CorporateMessenger.application.applicationService.AuthApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;


    @PostMapping(
            path = "/api/auth/registration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<SessionResponse> registration(
            HttpServletRequest servletReq,
            HttpServletResponse servletRes,
            @Valid @RequestBody RegistrationRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("RegistrationRequest validation error", bindingResult);
        }
        SessionResponse response = authApplicationService.registration(servletReq, servletRes, request);
        return ResponseEntity.status(201).body(response);
    }


    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<SessionResponse> login(
            HttpServletRequest servletReq,
            HttpServletResponse servletRes,
            @RequestBody LoginRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("LoginRequest validation error", bindingResult);
        }
        SessionResponse response = authApplicationService.login(servletReq, servletRes, request);
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> logout(
            HttpServletRequest servletReq,
            HttpServletResponse servletRes
    ) {
        authApplicationService.logout(servletReq, servletRes);
        return ResponseEntity.status(200).body("Logout success");
    }
}
