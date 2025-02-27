package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.Log;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@RequiredArgsConstructor
public class KafkaLogSerializer implements Serializer<Log> {

    private final ObjectMapper objectMapper;

    @Override
    public byte[] serialize(String topic, Log data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw new SerializationException("Error serializing Log object", e);
        }
    }
}
