package com.nagornov.CorporateMessenger.domain.broker.producer;

import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaLogRepository;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogProducer {

    private final KafkaLogRepository kafkaLogProducerRepository;

    public void sendMessage(@NonNull Log log) {
        kafkaLogProducerRepository.sendMessage(log);
    }

}
