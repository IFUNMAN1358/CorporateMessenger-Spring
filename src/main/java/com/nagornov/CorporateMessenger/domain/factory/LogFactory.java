package com.nagornov.CorporateMessenger.domain.factory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.nagornov.CorporateMessenger.domain.model.Log;
import com.nagornov.CorporateMessenger.infrastructure.logback.utils.ThrowableMessageUtils;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.Map;

@UtilityClass
public class LogFactory {

    public static Log createLogObject(ILoggingEvent eventObject, String serviceName) {

        final Instant instant = eventObject.getInstant();
        final String message = eventObject.getFormattedMessage();
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
                throwable == null ?
                        message :
                        ThrowableMessageUtils.formatErrorMessage(message, throwable.getMessage())
        );
        log.setLevel(level);

        log.setServiceName(serviceName);

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

}
