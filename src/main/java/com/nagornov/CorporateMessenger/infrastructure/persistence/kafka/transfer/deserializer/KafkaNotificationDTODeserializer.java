package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@RequiredArgsConstructor
public class KafkaNotificationDTODeserializer implements Deserializer<KafkaNotificationDTO> {

    private final ObjectMapper objectMapper;

    @Override
    public KafkaNotificationDTO deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, KafkaNotificationDTO.class);
        } catch (IOException e) {
            throw new SerializationException("KafkaNotificationDTODeserializer | Error deserializing KafkaNotificationDTO object", e);
        }
    }

}
