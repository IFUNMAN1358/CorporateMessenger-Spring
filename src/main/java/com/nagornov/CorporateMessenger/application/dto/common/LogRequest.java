package com.nagornov.CorporateMessenger.application.dto.common;

import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.Log;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogRequest {

    private Log log;

}
