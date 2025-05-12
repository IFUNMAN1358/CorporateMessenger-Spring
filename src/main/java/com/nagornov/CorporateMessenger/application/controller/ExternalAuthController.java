package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.ExternalAuthApplicationService;
import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExternalAuthController {

    private final ExternalAuthApplicationService externalAuthApplicationService;

    @PostMapping(
            path = "/api/external/v1/auth/login",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<JwtResponse> externalLogin(
            @NotNull @NotBlank @RequestParam String username,
            @NotNull @NotBlank @RequestParam String password,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("Username | Password requestParams validation error", bindingResult);
        }
        JwtResponse response = externalAuthApplicationService.externalLogin(username, password);
        return ResponseEntity.status(200).body(response);
    }

}