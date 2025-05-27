package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.user.UserSettingsRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserSettingsResponse;
import com.nagornov.CorporateMessenger.domain.dto.UserWithUserSettingsDTO;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import com.nagornov.CorporateMessenger.domain.service.user.UserSettingsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingsApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserSettingsService userSettingsService;


    @Transactional(readOnly = true)
    public UserSettingsResponse getMyUserSettings() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        UserWithUserSettingsDTO authDto = userService.getWithUserSettingsById(authInfo.getUserIdAsUUID());
        return new UserSettingsResponse(authDto.getUserSettings());
    }


    @Transactional
    public UserSettingsResponse changeMyUserSettings(@NonNull UserSettingsRequest request) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserWithUserSettingsDTO authDto = userService.getWithUserSettingsById(authInfo.getUserIdAsUUID());

        UserSettings authUserSettings = authDto.getUserSettings();

        authUserSettings.updateIsConfirmContactRequests(request.getIsConfirmContactRequests());
        authUserSettings.updateContactsVisibility(request.getContactsVisibility());
        authUserSettings.updateProfileVisibility(request.getProfileVisibility());
        authUserSettings.updateEmployeeVisibility(request.getEmployeeVisibility());
        authUserSettings.updateIsSearchable(request.getIsSearchable());

        return new UserSettingsResponse(userSettingsService.update(authUserSettings));
    }

}