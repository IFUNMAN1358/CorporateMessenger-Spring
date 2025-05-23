package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisSessionEntity implements Serializable {

    private String accessToken;
    private String refreshToken;
    private String csrfToken;
    private Instant createdAt;
    private Instant updatedAt;

}