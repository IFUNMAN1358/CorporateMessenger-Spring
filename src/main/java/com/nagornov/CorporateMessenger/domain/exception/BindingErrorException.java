package com.nagornov.CorporateMessenger.domain.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class BindingErrorException extends RuntimeException {

    private final BindingResult bindingResult;

    public BindingErrorException(BindingResult bindingResult) {

        super();
        this.bindingResult = bindingResult;

    }

}
