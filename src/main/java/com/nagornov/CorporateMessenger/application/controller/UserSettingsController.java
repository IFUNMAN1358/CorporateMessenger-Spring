package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.UserSettingsApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserSettingsRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserSettingsResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsApplicationService userSettingsApplicationService;


    @GetMapping(
            path = "/api/user/settings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserSettingsResponse> getMyUserSettings() {
        UserSettingsResponse response = userSettingsApplicationService.getMyUserSettings();
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/settings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserSettingsResponse> changeMyUserSettings(
            @RequestBody UserSettingsRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserSettingsRequest validation error", bindingResult);
        }
        UserSettingsResponse response = userSettingsApplicationService.changeMyUserSettings(request);
        return ResponseEntity.status(200).body(response);
    }

}
