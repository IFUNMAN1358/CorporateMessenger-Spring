package com.nagornov.CorporateMessenger.domain.logger;

import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaLogProducerService;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.LoggerMetadata;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.service.AbstractLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomainServiceLogger extends AbstractLogger {

    private final KafkaLogProducerService kafkaLogProducerService;

    @Override
    public LoggerMetadata initLoggerMetadata() {
        return new LoggerMetadata("domainService");
    }

    @Override
    public void sendLog(Log log) {
        kafkaLogProducerService.sendMessage(log);
    }
}
