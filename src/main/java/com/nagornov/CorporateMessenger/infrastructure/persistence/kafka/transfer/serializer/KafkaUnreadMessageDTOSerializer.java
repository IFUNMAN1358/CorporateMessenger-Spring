package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@RequiredArgsConstructor
public class KafkaUnreadMessageDTOSerializer implements Serializer<KafkaUnreadMessageDTO> {

    private final ObjectMapper objectMapper;

    @Override
    public byte[] serialize(String topic, KafkaUnreadMessageDTO data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException("KafkaUnreadMessageDTOSerializer | Error serializing KafkaUnreadMessageDTO", e);
        }
    }

}
