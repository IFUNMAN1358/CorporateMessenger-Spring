package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisUserBlacklistEntity implements Serializable {

    private UUID id;
    private UUID userId;
    private UUID blockedUserId;
    private Instant blockedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedisUserBlacklistEntity that = (RedisUserBlacklistEntity) o;
        return Objects.equals(userId, that.userId) && Objects.equals(blockedUserId, that.blockedUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, blockedUserId);
    }
}
