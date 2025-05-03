package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactRole;
import com.nagornov.CorporateMessenger.domain.enums.model.ContactStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaContactEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "contact_id", nullable = false, updatable = false)
    private UUID contactId;

    @Column(name = "role", length = 32, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ContactRole role;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactStatus status;

    @Column(name = "last_request_sent_at")
    private Instant lastRequestSentAt;

    @Column(name = "added_at")
    private Instant addedAt;

}
