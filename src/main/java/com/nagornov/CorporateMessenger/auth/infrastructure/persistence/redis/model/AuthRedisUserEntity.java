package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthRedisUserEntity {

    private UUID id;
    private UUID sessionId;

    public static AuthRedisUserEntity fromHash(Map<Object, Object> hash) {
        return new AuthRedisUserEntity(
                UUID.fromString((String) hash.get("id")),
                UUID.fromString((String) hash.get("sessionId"))
        );
    }

    public Map<Object, Object> toHash() {
        return Map.of(
                "id", this.id,
                "sessionId", this.sessionId
        );
    }

}
