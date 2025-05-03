package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@RequiredArgsConstructor
public class KafkaLogDeserializer implements Deserializer<Log> {

    private final ObjectMapper objectMapper;

    @Override
    public Log deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, Log.class);
        } catch (IOException e) {
            throw new SerializationException("KafkaLogDeserializer | Error deserializing Log object", e);
        }
    }

}
