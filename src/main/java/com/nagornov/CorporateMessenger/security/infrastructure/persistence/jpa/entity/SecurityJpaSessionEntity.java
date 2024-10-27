package com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityJpaSessionEntity {

    @Id
    @Column(name = "id", insertable = false, updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "access_token", nullable = false, unique = true, length = 512)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, unique = true, length = 512)
    private String refreshToken;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private SecurityJpaUserEntity user;

}
