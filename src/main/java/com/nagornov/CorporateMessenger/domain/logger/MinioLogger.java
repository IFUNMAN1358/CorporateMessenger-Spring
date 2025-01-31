package com.nagornov.CorporateMessenger.domain.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MinioLogger {

    private static final Logger minioLogger = LoggerFactory.getLogger("MinioLogger");

    public void debug(String message) {
        minioLogger.debug(message);
    }

    public void info(String message) {
        minioLogger.info(message);
    }

    public void warn(String message) {
        minioLogger.warn(message);
    }

    public void error(String message) {
        minioLogger.error(message);
    }

}
