package com.nagornov.CorporateMessenger.sharedKernel.LogService.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaLogProducerService;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.utils.LogLevelManager;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.utils.ThrowableMessageUtils;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@NoArgsConstructor
public class CustomKafkaAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> buffer = new ArrayList<>();
    private boolean initialized = false;

    private KafkaLogProducerService kafkaLogProducerService;
    private LogLevelManager logLevelManager;
    private String serviceName;

    private Log initLogObject(ILoggingEvent eventObject) {

        final Instant instant = eventObject.getInstant();
        final String message = eventObject.getMessage();
        final String level = eventObject.getLevel().levelStr;
        final IThrowableProxy throwable = eventObject.getThrowableProxy();

        final StackTraceElement[] callerData = eventObject.getCallerData();
        StackTraceElement caller = null;
        if (callerData != null && callerData.length > 0) {
            caller = callerData[0];
        }

        final Log log = new Log();

        log.setTimestamp(instant);
        log.setMessage(
                throwable == null ? message : ThrowableMessageUtils.formatErrorMessage(message, throwable.getMessage())
        );
        log.setLevel(level);

        log.setServiceName(serviceName);
        log.setDirectoryName("unknown");

        log.setFileName(caller.getFileName());
        log.setMethodName(caller.getMethodName());
        log.setLineNumber(caller.getLineNumber());

        Map<String, String> mdcMap = eventObject.getMDCPropertyMap();

        log.setTraceId(mdcMap.get("traceId"));
        log.setSpanId(mdcMap.get("spanId"));
        log.setUserId(mdcMap.get("userId"));
        log.setHttpMethod(mdcMap.get("httpMethod"));
        log.setHttpPath(mdcMap.get("httpPath"));

        return log;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {

        if (!initialized) {
            buffer.add(eventObject);
            return;
        }

        try {
            Log log = initLogObject(eventObject);
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
