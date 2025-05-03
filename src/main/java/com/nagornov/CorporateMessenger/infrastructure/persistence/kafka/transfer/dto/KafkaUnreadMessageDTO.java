package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaChatEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaUserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaUnreadMessageDTO {

    private KafkaChatEntity chat;
    private KafkaUserEntity user;
    private String operation;

}
