package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaUserEntity implements Serializable {

    private UUID id;
    private String username;
    private String password;
    private String phone;
    private String mainEmail;
    private Instant createdAt;
    private Instant updatedAt;

}
