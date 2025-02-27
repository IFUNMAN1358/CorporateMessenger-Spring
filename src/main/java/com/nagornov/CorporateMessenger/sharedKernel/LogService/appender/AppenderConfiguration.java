package com.nagornov.CorporateMessenger.sharedKernel.LogService.appender;

import ch.qos.logback.classic.LoggerContext;
import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaLogProducerService;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.utils.LogLevelManager;
import lombok.RequiredArgsConstructor;
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
    private final LogLevelManager logLevelManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // ASYNC
        var rootAppender =
                loggerContext.getLogger("ROOT"); // root
        CustomAsyncKafkaAppender asyncAppender =
                (CustomAsyncKafkaAppender) rootAppender.getAppender("ASYNC_KAFKA_APPENDER"); // async custom
        CustomKafkaAppender customKafkaAppender =
                (CustomKafkaAppender) asyncAppender.getAppender("KAFKA_APPENDER"); // custom

        customKafkaAppender.setKafkaLogProducerService(kafkaLogProducerService);
        customKafkaAppender.setLogLevelManager(logLevelManager);
        customKafkaAppender.setServiceName(serviceName);
        customKafkaAppender.start();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
