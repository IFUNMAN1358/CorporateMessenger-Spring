package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactsVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.EmployeeVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.ProfileVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaUserSettingsEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "is_confirm_contact_requests", nullable = false)
    private Boolean isConfirmContactRequests;

    @Column(name = "contacts_visibility", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactsVisibility contactsVisibility;

    @Column(name = "profile_visibility", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility;

    @Column(name = "employee_visibility", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeVisibility employeeVisibility;

    @Column(name = "is_searchable", nullable = false)
    private Boolean isSearchable;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
