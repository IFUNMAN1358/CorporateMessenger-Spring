package com.nagornov.CorporateMessenger.domain.service.domainService.kafka;

import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaLogProducerRepository;
import com.nagornov.CorporateMessenger.sharedKernel.logs.interfaces.LogSender;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLogSender implements LogSender {

    private final KafkaLogProducerRepository kafkaLogProducerRepository;

    @Override
    public void sendLog(Log log) {
        kafkaLogProducerRepository.sendMessage(log);
    }

}