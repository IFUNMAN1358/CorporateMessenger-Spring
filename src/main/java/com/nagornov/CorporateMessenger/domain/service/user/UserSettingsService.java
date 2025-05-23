package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactsVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.EmployeeVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.ProfileVisibility;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserSettingsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final JpaUserSettingsRepository jpaUserSettingsRepository;

    @Transactional
    public UserSettings create(@NonNull UUID userId) {
        UserSettings userSettings = new UserSettings(
                UUID.randomUUID(),
                userId,
                true,
                ContactsVisibility.EVERYONE,
                ProfileVisibility.EVERYONE,
                EmployeeVisibility.EVERYONE,
                true,
                Instant.now(),
                Instant.now()
        );
        return jpaUserSettingsRepository.save(userSettings);
    }

    @Transactional
    public UserSettings update(@NonNull UserSettings userSettings) {
        userSettings.updateUpdatedAtAsNow();
        return jpaUserSettingsRepository.save(userSettings);
    }

    public UserSettings getByUserId(@NonNull UUID userId) {
        return jpaUserSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSettings[userId=%s] not found".formatted(userId)));
    }

    public void ensureUserHasAccessToContacts(@NonNull UserSettings userSettings, boolean existsContactPair) {
        if (
                userSettings.isContactsVisibility(ContactsVisibility.ONLY_ME) ||
                (userSettings.isContactsVisibility(ContactsVisibility.CONTACTS) && !existsContactPair)
        ) {
            throw new ResourceBadRequestException("You can't get contacts");
        }
    }


    public void ensureUserHasAccessToProfile(@NonNull UserSettings userSettings, boolean existsContactPair) {
        if (
                userSettings.isProfileVisibility(ProfileVisibility.ONLY_ME) ||
                (userSettings.isProfileVisibility(ProfileVisibility.CONTACTS) && !existsContactPair)
        ) {
            throw new ResourceBadRequestException("You can't get profile");
        }
    }


    public void ensureUserHasAccessToEmployee(@NonNull UserSettings userSettings, boolean existsContactPair) {
        if (
                userSettings.isEmployeeVisibility(EmployeeVisibility.ONLY_ME) ||
                (userSettings.isEmployeeVisibility(EmployeeVisibility.CONTACTS) && !existsContactPair)
        ) {
            throw new ResourceBadRequestException("You can't get employee");
        }
    }
}