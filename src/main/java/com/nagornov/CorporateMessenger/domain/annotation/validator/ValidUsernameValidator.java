package com.nagornov.CorporateMessenger.domain.annotation.validator;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUsername;
import com.nagornov.CorporateMessenger.domain.annotation.enums.UsernameType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {

    private static final String USER_USERNAME_NOT_NULL = "Имя пользователя не может быть null";
    private static final String USER_USERNAME_NOT_EMPTY = "Имя пользователя не может быть пустым";
    private static final String USER_USERNAME_FIRST_LETTER = "Имя пользователя должно начинаться с буквы";
    private static final String USER_USERNAME_COMPOSITION = "Имя пользователя должно содержать только буквы, цифры и подчеркивания";
    private static final String USER_USERNAME_LAST_LETTER_OR_NUMBER = "Имя пользователя должно заканчиваться буквой или цифрой";
    private static final String USER_USERNAME_CONSECUTIVE_UNDERSCORES = "Имя пользователя не должно содержать последовательные подчеркивания";

    private static final String CHAT_USERNAME_NOT_NULL = "Уникальное имя чата не может быть null";
    private static final String CHAT_USERNAME_NOT_EMPTY = "Уникальное имя чата не может быть пустым";
    private static final String CHAT_USERNAME_FIRST_LETTER = "Уникальное имя чата должно начинаться с буквы";
    private static final String CHAT_USERNAME_COMPOSITION = "Уникальное имя чата должно содержать только буквы, цифры и подчеркивания";
    private static final String CHAT_USERNAME_LAST_LETTER_OR_NUMBER = "Уникальное имя чата должно заканчиваться буквой или цифрой";
    private static final String CHAT_USERNAME_CONSECUTIVE_UNDERSCORES = "Уникальное имя чата не должно содержать последовательные подчеркивания";

    private UsernameType usernameType;

    @Override
    public void initialize(ValidUsername constraintAnnotation) {
        this.usernameType = constraintAnnotation.usernameType();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (value == null) {
            context.buildConstraintViolationWithTemplate(
                    usernameType == UsernameType.USER ? USER_USERNAME_NOT_NULL : CHAT_USERNAME_NOT_NULL
                    )
                    .addConstraintViolation();
            return false;
        }

        if (value.isEmpty()) {
            context.buildConstraintViolationWithTemplate(
                    usernameType == UsernameType.USER ? USER_USERNAME_NOT_EMPTY : CHAT_USERNAME_NOT_EMPTY
                    )
                    .addConstraintViolation();
            return false;
        }

        if (!value.matches("^[a-zA-Z].*")) {
            context.buildConstraintViolationWithTemplate(
                    usernameType == UsernameType.USER ? USER_USERNAME_FIRST_LETTER : CHAT_USERNAME_FIRST_LETTER
                    )
                   .addConstraintViolation();
            return false;
        }

        if (!value.matches("^[a-zA-Z0-9_]*$")) {
            context.buildConstraintViolationWithTemplate(
                    usernameType == UsernameType.USER ? USER_USERNAME_COMPOSITION : CHAT_USERNAME_COMPOSITION
                    )
                   .addConstraintViolation();
            return false;
        }

        if (!value.matches(".*[a-zA-Z0-9]$")) {
            context.buildConstraintViolationWithTemplate(
                    usernameType == UsernameType.USER ? USER_USERNAME_LAST_LETTER_OR_NUMBER : CHAT_USERNAME_LAST_LETTER_OR_NUMBER
                    )
                   .addConstraintViolation();
            return false;
        }

        if (value.contains("__")) {
            context.buildConstraintViolationWithTemplate(
                    usernameType == UsernameType.USER ? USER_USERNAME_CONSECUTIVE_UNDERSCORES : CHAT_USERNAME_CONSECUTIVE_UNDERSCORES
                    )
                   .addConstraintViolation();
            return false;
        }

        return true;
    }

}
