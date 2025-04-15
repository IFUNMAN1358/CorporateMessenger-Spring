package com.nagornov.CorporateMessenger.infrastructure.configuration.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaLogProducerService;
import com.nagornov.CorporateMessenger.infrastructure.logback.appender.CustomAsyncKafkaAppender;
import com.nagornov.CorporateMessenger.infrastructure.logback.appender.CustomKafkaAppender;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppenderConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.application.name}")
    private String serviceName;

    private final KafkaLogProducerService kafkaLogProducerService;

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger rootAppender = null;
        CustomAsyncKafkaAppender asyncKafkaAppender = null;
        CustomKafkaAppender kafkaAppender = null;

        rootAppender = loggerContext.getLogger("ROOT");

        if (rootAppender != null) {
            asyncKafkaAppender = (CustomAsyncKafkaAppender) rootAppender.getAppender("ASYNC_KAFKA_APPENDER");
        }

        if (asyncKafkaAppender != null) {
            kafkaAppender = (CustomKafkaAppender) asyncKafkaAppender.getAppender("KAFKA_APPENDER");

            kafkaAppender.setKafkaLogProducerService(kafkaLogProducerService);
            kafkaAppender.setServiceName(serviceName);
            kafkaAppender.start();
        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
