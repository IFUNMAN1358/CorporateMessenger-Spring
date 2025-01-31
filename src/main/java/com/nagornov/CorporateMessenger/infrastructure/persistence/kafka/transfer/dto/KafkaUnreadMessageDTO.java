package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaChatEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaUnreadMessageDTO {

    private KafkaUserEntity user;
    private KafkaChatEntity chat;
    private String operation;

}
