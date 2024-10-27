package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registration_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthJpaRegistrationKeyEntity {

    @Id
    @Column(name = "id", insertable = false, updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "value", nullable = false, unique = true, updatable = false)
    private String value;

    @Column(name = "is_applied", nullable = false)
    private Boolean isApplied = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //
    // Methods
    //

    @PrePersist
    public void createTimestamps() {
        createdAt = LocalDateTime.now();
    }
}