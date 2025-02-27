package com.nagornov.CorporateMessenger.sharedKernel.LogService.utils;

import com.nagornov.CorporateMessenger.sharedKernel.LogService.enums.BaseLogLevel;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.interfaces.LogLevel;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.properties.LogProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LogLevelManager {

    private final LogProperties logProperties;

    private static final List<String> basedLevels = List.of(
            BaseLogLevel.OFF.getLevel(),
            BaseLogLevel.ALL.getLevel(),
            BaseLogLevel.TRACE.getLevel(),
            BaseLogLevel.DEBUG.getLevel(),
            BaseLogLevel.INFO.getLevel(),
            BaseLogLevel.WARN.getLevel(),
            BaseLogLevel.ERROR.getLevel(),
            BaseLogLevel.FATAL.getLevel()
    );

    public boolean isAccessibleLevel(LogLevel logLevel) {
        List<String> selectedLevels = logProperties.getLevel();

        // If the OFF level is selected, no logs are written
        if (selectedLevels.contains(BaseLogLevel.OFF.getLevel())) {
            return false;
        }

        // For non-standard levels, we always allow their logging
        if (!(logLevel instanceof BaseLogLevel)) {
            return true;
        }

        // If there are no selected levels, then we log everything
        if (selectedLevels.isEmpty()) {
            return true;
        }

        String currentLevel = logLevel.getLevel();
        int logLevelIndex = basedLevels.indexOf(currentLevel);

        // If the level is not found in the list of base levels, skip it
        if (logLevelIndex == -1) {
            return false;
        }

        // If only one level is selected, log all levels equal to or higher than the selected one
        if (selectedLevels.size() == 1) {
            return logLevelIndex >= basedLevels.indexOf(selectedLevels.getFirst());
        }

        // We log only the selected levels
        return selectedLevels.contains(currentLevel);
    }

}