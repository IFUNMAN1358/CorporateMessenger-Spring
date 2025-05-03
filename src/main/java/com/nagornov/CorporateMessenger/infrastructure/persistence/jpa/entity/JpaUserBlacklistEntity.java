package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_blacklist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaUserBlacklistEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "blocked_user_id", nullable = false, updatable = false)
    private UUID blockedUserId;

    @Column(name = "blocked_at", nullable = false, updatable = false)
    private Instant blockedAt;

}
