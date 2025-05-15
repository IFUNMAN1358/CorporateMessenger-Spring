package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.ExternalServiceApplicationService;
import com.nagornov.CorporateMessenger.application.dto.auth.ExternalServiceRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.ExternalServiceResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class ExternalServiceController {

    private final ExternalServiceApplicationService externalServiceApplicationService;


    @PostMapping(
            path = "/api/external-service",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ExternalServiceResponse> create(
            @NotNull @RequestBody ExternalServiceRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("ExternalServiceRequest validation error", bindingResult);
        }
        ExternalServiceResponse response = externalServiceApplicationService.create(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/external-service/{serviceName}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ExternalServiceResponse> getByServiceName(
            @NotNull @PathVariable String serviceName,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(serviceName) validation error", bindingResult);
        }
        ExternalServiceResponse response = externalServiceApplicationService.getByServiceName(serviceName);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/external-services",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<ExternalServiceResponse>> findAll() {
        List<ExternalServiceResponse> response = externalServiceApplicationService.findAll();
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/external-service/{serviceName}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ExternalServiceResponse> generateNewApiKeyByServiceName(
            @NotNull @PathVariable String serviceName,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(serviceName) validation error", bindingResult);
        }
        ExternalServiceResponse response =
                externalServiceApplicationService.generateNewApiKeyByServiceName(serviceName);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            path = "/api/external-service/{serviceName}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteByServiceName(
            @NotNull @PathVariable String serviceName,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(serviceName) validation error", bindingResult);
        }
        externalServiceApplicationService.deleteByServiceName(serviceName);
        return ResponseEntity.status(204).body("ExternalService deleted");
    }
}