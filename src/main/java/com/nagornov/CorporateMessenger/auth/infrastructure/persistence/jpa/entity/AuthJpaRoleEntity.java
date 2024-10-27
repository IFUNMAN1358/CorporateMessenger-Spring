package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthJpaRoleEntity {

    @Id
    @Column(name = "id", insertable = false, updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<AuthJpaUserEntity> users;

    //
    // Methods
    //

    @PrePersist
    public void createTimestamps() {
        createdAt = LocalDateTime.now();
    }
}