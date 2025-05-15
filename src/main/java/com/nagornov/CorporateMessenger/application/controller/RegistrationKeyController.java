package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.RegistrationKeyApplicationService;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationKeyResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
            @RequestParam int size,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("RequestParam(page) | RequestParam(size) validation error", bindingResult);
        }
        List<RegistrationKeyResponse> response = registrationKeyApplicationService.findAll(page, size);
        return ResponseEntity.status(201).body(response);
    }


    @DeleteMapping(
            path = "/api/registration-key/{keyId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteByKeyId(
            @NotNull @PathVariable UUID keyId,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(keyId) validation error", bindingResult);
        }
        registrationKeyApplicationService.deleteByKeyId(keyId);
        return ResponseEntity.status(204).body("Registration key deleted");
    }
}