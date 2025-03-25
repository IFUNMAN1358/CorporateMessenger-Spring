package com.nagornov.CorporateMessenger.domain.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    private String path;
    private String message;
    private int statusCode;
    private Map<String, String> validationErrors;
    private LocalDateTime localDateTime;

}