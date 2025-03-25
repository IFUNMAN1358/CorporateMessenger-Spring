package com.nagornov.CorporateMessenger.infrastructure.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaLogProducerService;
import com.nagornov.CorporateMessenger.infrastructure.logback.factory.LogFactory;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@NoArgsConstructor
public class CustomKafkaAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> buffer = new ArrayList<>();
    private boolean initialized = false;

    private KafkaLogProducerService kafkaLogProducerService;
    private String serviceName;

    @Override
    protected void append(ILoggingEvent eventObject) {

        if (!initialized) {
            buffer.add(eventObject);
            return;
        }

        try {
            Log log = LogFactory.createLogObject(eventObject, serviceName);
            kafkaLogProducerService.sendMessage(log);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void start() {
        super.start();
        initialized = true;
        if (!buffer.isEmpty() && kafkaLogProducerService != null) {
            buffer.forEach(this::append);
            buffer.clear();
        }
    }
}
