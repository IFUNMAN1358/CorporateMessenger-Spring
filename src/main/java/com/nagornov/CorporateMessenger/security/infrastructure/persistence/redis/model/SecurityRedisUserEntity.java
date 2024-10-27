package com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class SecurityRedisUserEntity {

    private UUID id;
    private UUID sessionId;

    public static SecurityRedisUserEntity fromHash(Map<Object, Object> hash) {
        return new SecurityRedisUserEntity(
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
