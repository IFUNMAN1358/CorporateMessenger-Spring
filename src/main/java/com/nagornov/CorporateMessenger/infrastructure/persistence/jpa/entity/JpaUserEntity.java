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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JpaUserEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", length = 32, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "main_email", length = 321, unique = true)
    private String mainEmail;

    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 64)
    private String lastName;

    @Column(name = "bio", length = 70)
    private String bio;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}