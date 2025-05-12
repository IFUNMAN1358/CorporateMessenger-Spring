package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.RegistrationKeyApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
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
public class RegistrationController {

    private final RegistrationKeyApplicationService registrationKeyApplicationService;


    @PostMapping(
            path = "/api/auth/registration-key",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<RegistrationKey> createRegistrationKey() {
        RegistrationKey response = registrationKeyApplicationService.createRegistrationKey();
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/auth/registration-keys",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<RegistrationKey>> findAllRegistrationKeys(
            @RequestParam int page,
            @RequestParam int size,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("RequestParam(page) | RequestParam(size) validation error", bindingResult);
        }
        List<RegistrationKey> response = registrationKeyApplicationService.findAllRegistrationKeys(page, size);
        return ResponseEntity.status(201).body(response);
    }


    @DeleteMapping(
            path = "/api/auth/registration-key/{keyId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteRegistrationKeyById(
            @NotNull @PathVariable UUID keyId,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(keyId) validation error", bindingResult);
        }
        registrationKeyApplicationService.deleteRegistrationKeyById(keyId);
        return ResponseEntity.status(204).body("Registration key deleted");
    }
}