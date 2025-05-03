package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@RequiredArgsConstructor
public class KafkaUnreadMessageDTODeserializer implements Deserializer<KafkaUnreadMessageDTO> {

    private final ObjectMapper objectMapper;

    @Override
    public KafkaUnreadMessageDTO deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, KafkaUnreadMessageDTO.class);
        } catch (IOException e) {
            throw new SerializationException("KafkaUnreadMessageDTODeserializer | Error deserializing KafkaUnreadMessageDTO object", e);
        }
    }

}
