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
import java.util.UUID;

@Entity
@Table(name = "external_services")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaExternalServiceEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 32, unique = true, nullable = false)
    private String name;

    @Column(name = "api_key", length = 64, unique = true)
    private String apiKey;

    @Column(name = "requires_api_key", nullable = false)
    private Boolean requiresApiKey;
}