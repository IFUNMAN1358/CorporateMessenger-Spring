package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.EmployeeApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.employee.EmployeeWithEmployeePhotoResponse;
import com.nagornov.CorporateMessenger.application.dto.model.employee.UpdateEmployeeRequest;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeApplicationService employeeApplicationService;


    @GetMapping(
            path = "/api/user/employee",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<EmployeeWithEmployeePhotoResponse> getMyEmployee() {
        EmployeeWithEmployeePhotoResponse response = employeeApplicationService.getMyEmployee();
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/employee",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<EmployeeWithEmployeePhotoResponse> getEmployeeByUserId(@NotNull @PathVariable("userId") UUID userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(userId) validation error", bindingResult);
        }
        EmployeeWithEmployeePhotoResponse response = employeeApplicationService.getEmployeeByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/employee",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<EmployeeWithEmployeePhotoResponse> updateMyEmployee(@RequestBody UpdateEmployeeRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UpdateEmployeeRequest validation error", bindingResult);
        }
        EmployeeWithEmployeePhotoResponse response = employeeApplicationService.updateMyEmployee(request);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            path = "/api/user/employee",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<EmployeeWithEmployeePhotoResponse> clearMyEmployee() {
        EmployeeWithEmployeePhotoResponse response = employeeApplicationService.clearMyEmployee();
        return ResponseEntity.status(200).body(response);
    }
}