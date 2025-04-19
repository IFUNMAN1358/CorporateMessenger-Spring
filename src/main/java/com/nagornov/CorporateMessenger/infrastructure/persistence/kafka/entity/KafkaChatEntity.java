package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaChatEntity implements Serializable {

    private Long id;
    private String type;
    private String username;
    private String title;
    private String description;
    private String inviteLink;
    private Boolean joinByRequest;
    private Boolean hasHiddenMembers;
    private Instant createdAt;
    private Instant updatedAt;

}
