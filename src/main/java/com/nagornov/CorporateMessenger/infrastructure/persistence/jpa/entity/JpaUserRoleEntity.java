package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "user_role",
        uniqueConstraints = @UniqueConstraint(name = "unique_user_role", columnNames = {"user_id", "role_id"})
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaUserRoleEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "role_id", updatable = false, nullable = false)
    private UUID roleId;

}
