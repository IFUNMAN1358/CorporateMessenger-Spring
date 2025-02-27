package com.nagornov.CorporateMessenger.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    private String path;
    private String message;
    private int statusCode;
    private LocalDateTime localDateTime;

    @Override
    public String toString() {
        return "ApiError{" +
                "path='" + path + '\'' +
                ", message='" + message + '\'' +
                ", statusCode=" + statusCode + '\'' +
                ", localDateTime=" + localDateTime + '\'' +
                '}';
    }
}