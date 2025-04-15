package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaChatEntity {

    private String chatType;
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private UUID lastMessageId;
    private Boolean hasPhotos;
    private Boolean isPublic;
    private UUID firstUserId;
    private UUID secondUserId;
    private Boolean isAvailable;
    private Instant updatedAt;
    private Instant createdAt;

}
