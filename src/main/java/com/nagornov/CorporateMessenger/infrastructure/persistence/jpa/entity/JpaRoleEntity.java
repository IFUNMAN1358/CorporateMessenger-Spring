package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JpaRoleEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}