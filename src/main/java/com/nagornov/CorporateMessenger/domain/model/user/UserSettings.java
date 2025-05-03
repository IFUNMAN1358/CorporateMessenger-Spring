package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactsVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.ProfileVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserSettings {

    private UUID id;
    private UUID userId;
    private Boolean isConfirmContactRequests;
    private ContactsVisibility contactsVisibility;
    private ProfileVisibility profileVisibility;
    private Boolean isSearchable;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isContactsVisibility(ContactsVisibility visibility) {
        return this.contactsVisibility.equals(visibility);
    }

    public boolean isProfileVisibility(ProfileVisibility visibility) {
        return this.profileVisibility.equals(visibility);
    }

    public void updateUpdatedAtAsNow() {
        this.updatedAt = Instant.now();
    }

}
