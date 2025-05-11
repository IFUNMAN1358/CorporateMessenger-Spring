package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

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

    public Map<Object, Object> toMap() {
        return Map.of(
                "accessToken", this.accessToken,
                "refreshToken", this.refreshToken,
                "csrfToken", this.csrfToken,
                "createdAt", this.createdAt.toString(),
                "updatedAt", this.updatedAt.toString()
        );
    }

    public static RedisSessionEntity fromMap(Map<Object, Object> map) {
        return new RedisSessionEntity(
                (String) map.get("accessToken"),
                (String) map.get("refreshToken"),
                (String) map.get("csrfToken"),
                Instant.parse((String) map.get("createdAt")),
                Instant.parse((String) map.get("updatedAt"))
        );
    }

}
