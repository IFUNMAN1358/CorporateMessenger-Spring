package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.ExternalAuthApplicationService;
import com.nagornov.CorporateMessenger.application.dto.auth.ExternalAuthResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class ExternalAuthController {

    private final ExternalAuthApplicationService externalAuthApplicationService;


    @PostMapping(
            path = "/api/external/v1/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ExternalAuthResponse> externalLogin(
            HttpServletRequest servletReq,
            HttpServletResponse servletRes,
            @RequestBody LoginRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("LoginRequest validation error", bindingResult);
        }
        ExternalAuthResponse response = externalAuthApplicationService.externalLogin(servletReq, servletRes, request);
        return ResponseEntity.status(200).body(response);
    }

}