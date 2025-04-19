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
@Table(
    name = "contacts",
    uniqueConstraints = @UniqueConstraint(name = "unique_contact", columnNames = {"user_id", "contact_id"})
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaContactEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "contact_id", updatable = false, nullable = false)
    private UUID contactId;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "added_at", updatable = false, nullable = false)
    private Instant addedAt;

}
