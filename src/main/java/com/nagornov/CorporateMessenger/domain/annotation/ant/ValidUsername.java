package com.nagornov.CorporateMessenger.domain.annotation.ant;


import com.nagornov.CorporateMessenger.domain.annotation.enums.UsernameType;
import com.nagornov.CorporateMessenger.domain.annotation.validator.ValidUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "Некорректный username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    UsernameType usernameType();
}