package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactRole;
import com.nagornov.CorporateMessenger.domain.enums.model.ContactStatus;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Contact {

    private UUID id;
    private UUID userId;
    private UUID contactId;
    private ContactRole role;
    private ContactStatus status;
    private Instant lastRequestSentAt;
    private Instant addedAt;

    // role

    public boolean isInitiator() {
        return this.role.equals(ContactRole.INITIATOR);
    }

    public boolean isRecipient() {
        return this.role.equals(ContactRole.RECIPIENT);
    }

    // status

    public boolean isPending() {
        return this.status.equals(ContactStatus.PENDING);
    }

    public boolean isConfirmed() {
        return this.status.equals(ContactStatus.CONFIRMED);
    }

    public void confirm() {
        this.status = ContactStatus.CONFIRMED;
    }

    //

    public void updateLastRequestSentAtAsNow() {
        this.lastRequestSentAt = Instant.now();
    }

    public void updateAddedAsNow() {
        if (this.addedAt != null) {
            throw new ResourceConflictException("Contact[addedAt=%s] already defined and cannot be updated".formatted(this.addedAt));
        }
        this.addedAt = Instant.now();
    }

}
