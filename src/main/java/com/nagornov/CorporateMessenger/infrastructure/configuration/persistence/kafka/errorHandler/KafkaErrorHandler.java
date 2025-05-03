package com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.kafka.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaErrorHandler implements CommonErrorHandler {

    @Override
    public boolean handleOne(
            @NotNull Exception exception,
            @NotNull ConsumerRecord<?, ?> record,
            @NotNull Consumer<?, ?> consumer,
            @NotNull MessageListenerContainer container
    ) {
        handle(exception, consumer);
        return false;
    }

    @Override
    public void handleOtherException(
            @NotNull Exception exception,
            @NotNull Consumer<?, ?> consumer,
            @NotNull MessageListenerContainer container,
            boolean batchListener
    ) {
        handle(exception, consumer);
    }

    private void handle(Exception exception, Consumer<?, ?> consumer) {
        log.error("Exception thrown", exception);
        if (exception instanceof RecordDeserializationException ex) {
            consumer.seek(ex.topicPartition(), ex.offset() + 1L);
            consumer.commitSync();
        } else {
            log.error("Exception not handled", exception);
        }
    }

}
