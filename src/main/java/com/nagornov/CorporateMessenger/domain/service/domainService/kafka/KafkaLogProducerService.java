package com.nagornov.CorporateMessenger.domain.service.domainService.kafka;

import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaLogProducerRepository;
import com.nagornov.CorporateMessenger.domain.model.Log;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLogProducerService {

    private final KafkaLogProducerRepository kafkaLogProducerRepository;

    public void sendMessage(@NotNull Log log) {
        kafkaLogProducerRepository.sendMessage(log);
    }

}
