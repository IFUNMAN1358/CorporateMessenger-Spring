package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthJpaSessionEntity {

    @Id
    @Column(name = "id", insertable = false, updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "access_token", nullable = false, unique = true, length = 512)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, unique = true, length = 512)
    private String refreshToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthJpaUserEntity user;

    //
    // Methods
    //

    @PrePersist
    public void createTimestamps() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateTimestamps() {
        updatedAt = LocalDateTime.now();
    }
}