package com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class SecurityRedisSessionEntity {

    private UUID id;
    private String accessToken;
    private String refreshToken;

    public static SecurityRedisSessionEntity fromHash(Map<Object, Object> hash) {
        return new SecurityRedisSessionEntity(
                UUID.fromString((String) hash.get("id")),
                (String) hash.get("accessToken"),
                (String) hash.get("refreshToken")
        );
    }

    public Map<Object, Object> toHash() {
        return Map.of(
                "id", this.id,
                "accessToken", this.accessToken,
                "refreshToken", this.refreshToken
        );
    }

}
