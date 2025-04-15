package com.nagornov.CorporateMessenger.domain.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;
@Getter
public class BindingErrorException extends RuntimeException {

    private final BindingResult bindingResult;

    public BindingErrorException(String message, BindingResult bindingResult) {

        super(message);
        this.bindingResult = bindingResult;

    }

}
