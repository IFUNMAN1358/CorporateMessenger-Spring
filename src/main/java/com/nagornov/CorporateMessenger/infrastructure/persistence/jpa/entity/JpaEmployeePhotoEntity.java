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
@Table(name = "employee_photos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaEmployeePhotoEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "employee_id", nullable = false, updatable = false)
    private UUID employeeId;

    @Column(name = "file_name", nullable = false, updatable = false)
    private String fileName;

    @Column(name = "small_file_path", nullable = false)
    private String smallFilePath;

    @Column(name = "small_file_size", nullable = false)
    private Long smallFileSize;

    @Column(name = "big_file_path", nullable = false)
    private String bigFilePath;

    @Column(name = "big_file_size", nullable = false)
    private Long bigFileSize;

    @Column(name = "mime_type", length = 32, nullable = false)
    private String mimeType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
