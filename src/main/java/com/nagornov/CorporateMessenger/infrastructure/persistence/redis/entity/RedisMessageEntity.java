package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

}
