package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisJwtSessionEntity {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Map<Object, Object> toMap() {
        return Map.of(
                "accessToken", (String) this.accessToken,
                "refreshToken", (String) this.refreshToken,
                "createdAt", this.createdAt.toString(),
                "updatedAt", this.updatedAt.toString()
        );
    }

    public static RedisJwtSessionEntity fromMap(Map<Object, Object> map) {
        return new RedisJwtSessionEntity(
                (String) map.get("accessToken"),
                (String) map.get("refreshToken"),
                LocalDateTime.parse((String) map.get("createdAt")),
                LocalDateTime.parse((String) map.get("updatedAt"))
        );
    }

}
