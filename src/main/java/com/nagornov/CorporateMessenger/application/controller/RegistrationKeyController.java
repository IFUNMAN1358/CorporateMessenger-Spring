package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.RegistrationKeyApplicationService;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class RegistrationKeyController {

    private final RegistrationKeyApplicationService registrationKeyApplicationService;


    @PostMapping(
            path = "/api/registration-key",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<RegistrationKeyResponse> create() {
        RegistrationKeyResponse response = registrationKeyApplicationService.create();
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/registration-keys",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<RegistrationKeyResponse>> findAll(
            @RequestParam int page,
            @RequestParam int size
    ) {
        List<RegistrationKeyResponse> response = registrationKeyApplicationService.findAll(page, size);
        return ResponseEntity.status(201).body(response);
    }


    @DeleteMapping(
            path = "/api/registration-key/{keyId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteByKeyId(
            @PathVariable UUID keyId
    ) {
        registrationKeyApplicationService.deleteByKeyId(keyId);
        return ResponseEntity.status(204).body("Registration key deleted");
    }
}