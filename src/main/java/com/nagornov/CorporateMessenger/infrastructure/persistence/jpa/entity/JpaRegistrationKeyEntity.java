package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "registration_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JpaRegistrationKeyEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(name = "value", nullable = false, unique = true, updatable = false)
    private String value;

    @Column(name = "is_applied", nullable = false)
    private Boolean isApplied;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}