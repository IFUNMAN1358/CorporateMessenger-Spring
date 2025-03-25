package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profile_photos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaUserProfilePhotoEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "is_main", nullable = false)
    private Boolean isMain;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}