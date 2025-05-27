package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisMessageEntity implements Serializable {

    private UUID id;
    private Long chatId;
    private UUID senderId;
    private String senderUsername;
    private String content;
    private Boolean hasFiles;
    private Boolean isChanged;
    private Boolean isRead;
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedisMessageEntity that = (RedisMessageEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
