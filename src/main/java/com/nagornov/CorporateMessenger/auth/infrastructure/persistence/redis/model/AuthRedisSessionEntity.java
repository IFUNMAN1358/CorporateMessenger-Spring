package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthRedisSessionEntity {

    private UUID id;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AuthRedisSessionEntity fromHash(Map<Object, Object> hash) {
        return new AuthRedisSessionEntity(
                UUID.fromString((String) hash.get("id")),
                (String) hash.get("accessToken"),
                (String) hash.get("refreshToken"),
                LocalDateTime.parse((String) hash.get("createdAt")),
                LocalDateTime.parse((String) hash.get("updatedAt"))
        );
    }

    public Map<Object, Object> toHash() {
        return Map.of(
                "id", this.id,
                "accessToken", this.accessToken,
                "refreshToken", this.refreshToken,
                "createdAt", this.createdAt,
                "updatedAt", this.updatedAt
        );
    }

}
