package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class RedisSessionMapper {

    public Map<Object, Object> toHash(Session session) {
        return Map.of(
                "id", session.getId(),
                "accessToken", session.getAccessToken(),
                "refreshToken", session.getRefreshToken(),
                "createdAt", session.getCreatedAt(),
                "updatedAt", session.getUpdatedAt()
        );
    }

    public Session fromHash(Map<Object, Object> hash) {
        return new Session(
                UUID.fromString((String) hash.get("id")),
                (String) hash.get("accessToken"),
                (String) hash.get("refreshToken"),
                LocalDateTime.parse((String) hash.get("createdAt")),
                LocalDateTime.parse((String) hash.get("updatedAt"))
        );
    }

}
