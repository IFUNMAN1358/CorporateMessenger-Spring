package com.nagornov.CorporateMessenger.domain.service.domainService.kafka;

import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaLogProducerRepository;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLogProducerService {

    private final KafkaLogProducerRepository kafkaLogProducerRepository;

    public void sendMessage(@NonNull Log log) {
        kafkaLogProducerRepository.sendMessage(log);
    }

}
