package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.CsrfApplicationService;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CsrfController {

    private final CsrfApplicationService csrfApplicationService;

    @GetMapping(
            path = "/api/csrf-token",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        HttpResponse res = csrfApplicationService.getCsrfToken(request, response);
        return ResponseEntity.status(200).body(res);
    }

}
