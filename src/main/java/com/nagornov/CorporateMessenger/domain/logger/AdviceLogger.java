package com.nagornov.CorporateMessenger.domain.logger;

import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaLogProducerService;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.LoggerMetadata;
import com.nagornov.CorporateMessenger.sharedKernel.logs.service.AbstractLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdviceLogger extends AbstractLogger {

    private final KafkaLogProducerService kafkaLogProducerService;

    @Override
    protected LoggerMetadata initLoggerMetadata() {
        return new LoggerMetadata("advice");
    }

    @Override
    protected void sendLog(Log log) {
        kafkaLogProducerService.sendMessage(log);
    }
}
