package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RedisMessage {

    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String senderFirstName;
    private String senderUsername;
    private String content;
    private Boolean hasFiles;
    private Boolean isChanged;
    private Boolean isRead;
    private Instant createdAt;

}
