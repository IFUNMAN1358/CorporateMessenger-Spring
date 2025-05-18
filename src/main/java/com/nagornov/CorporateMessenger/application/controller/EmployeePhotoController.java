package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.EmployeePhotoApplicationService;
import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.model.employee.EmployeePhotoResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class EmployeePhotoController {

    private final EmployeePhotoApplicationService employeePhotoApplicationService;


    @PostMapping(
            path = "/api/user/employee/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<EmployeePhotoResponse> uploadOrUpdateMyEmployeePhoto(
            @ModelAttribute FileRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("FileRequest validation error", bindingResult);
        }
        EmployeePhotoResponse response = employeePhotoApplicationService.uploadOrUpdateMyEmployeePhoto(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/user/employee/photo",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMyEmployeePhoto(
            @RequestParam String size // big/small
    ) {
        Resource response = employeePhotoApplicationService.downloadMyEmployeePhoto(size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/employee/photo",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadEmployeePhotoByUserId(
            @PathVariable UUID userId,
            @RequestParam String size // big/small
    ) {
        Resource response = employeePhotoApplicationService.downloadEmployeePhotoByUserId(userId, size);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            path = "/api/user/employee/photo",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteMyEmployeePhoto() {
        employeePhotoApplicationService.deleteMyEmployeePhoto();
        return ResponseEntity.status(204).body("Employee photo deleted");
    }
}