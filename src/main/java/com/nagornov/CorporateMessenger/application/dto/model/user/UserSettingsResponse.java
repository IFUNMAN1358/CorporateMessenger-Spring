package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactsVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.EmployeeVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.ProfileVisibility;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserSettingsResponse {

    private UUID id;
    private UUID userId;
    private Boolean isConfirmContactRequests;
    private ContactsVisibility contactsVisibility;
    private ProfileVisibility profileVisibility;
    private EmployeeVisibility employeeVisibility;
    private Boolean isSearchable;
    private Instant createdAt;
    private Instant updatedAt;


    public UserSettingsResponse(@NonNull UserSettings userSettings) {
        this.id = userSettings.getId();
        this.userId = userSettings.getUserId();
        this.isConfirmContactRequests = userSettings.getIsConfirmContactRequests();
        this.contactsVisibility = userSettings.getContactsVisibility();
        this.profileVisibility = userSettings.getProfileVisibility();
        this.employeeVisibility = userSettings.getEmployeeVisibility();
        this.isSearchable = userSettings.getIsSearchable();
        this.createdAt = userSettings.getCreatedAt();
        this.updatedAt = userSettings.getUpdatedAt();
    }

}
