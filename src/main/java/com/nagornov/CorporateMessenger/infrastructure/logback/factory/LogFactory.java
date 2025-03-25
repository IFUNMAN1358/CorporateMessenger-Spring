package com.nagornov.CorporateMessenger.infrastructure.logback.factory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import com.nagornov.CorporateMessenger.infrastructure.logback.utils.LogUtils;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.Map;

@UtilityClass
public class LogFactory {

    public static Log createLogObject(ILoggingEvent eventObject, String serviceName) {

        final Map<String, String> mdcMap = eventObject.getMDCPropertyMap();
        final Instant instant = eventObject.getInstant();
        final String message = eventObject.getFormattedMessage();
        final String level = eventObject.getLevel().levelStr;
        final IThrowableProxy throwable = eventObject.getThrowableProxy();
        final StackTraceElement[] callerData = eventObject.getCallerData();

        final Log log = new Log();

        log.setTimestamp(instant);
        log.setMessage(
                throwable == null ?
                        message : LogUtils.formatErrorMessage(message, throwable.getMessage())
        );
        log.setLevel(level);
        log.setServiceName(serviceName);

        boolean isAspectLogger = Boolean.parseBoolean(mdcMap.get("isAspectLogger"));

        if (isAspectLogger) {
            log.setFileName(mdcMap.get("className"));
            log.setMethodName(mdcMap.get("methodName"));
            log.setLineNumber(Integer.parseInt(mdcMap.get("lineNumber")));
        } else {
            log.setFileName(callerData[0].getFileName());
            log.setMethodName(callerData[0].getMethodName());
            log.setLineNumber(callerData[0].getLineNumber());
        }

        log.setTraceId(mdcMap.get("traceId"));
        log.setSpanId(mdcMap.get("spanId"));
        log.setUserId(mdcMap.get("userId"));
        log.setHttpMethod(mdcMap.get("httpMethod"));
        log.setHttpPath(mdcMap.get("httpPath"));

        return log;
    }

}
