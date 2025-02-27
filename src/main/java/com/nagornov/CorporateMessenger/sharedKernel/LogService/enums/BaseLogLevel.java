package com.nagornov.CorporateMessenger.sharedKernel.LogService.enums;

import com.nagornov.CorporateMessenger.sharedKernel.LogService.interfaces.LogLevel;
import lombok.Getter;

@Getter
public enum BaseLogLevel implements LogLevel {

    OFF("OFF"),
    ALL("ALL"),
    TRACE("TRACE"),
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR"),
    FATAL("FATAL");

    private final String level;

    BaseLogLevel(String level) {
        this.level = level;
    }

}
