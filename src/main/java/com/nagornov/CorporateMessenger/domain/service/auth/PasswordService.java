package com.nagornov.CorporateMessenger.domain.service.auth;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(@NonNull String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchEncodedPassword(@NonNull String rawPassword, @NonNull String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void ensureMatchEncodedPassword(@NonNull String rawPassword, @NonNull String encodedPassword) {
        if (!matchEncodedPassword(rawPassword, encodedPassword)) {
            throw new ResourceConflictException(
                    "Incorrect password",
                    new FieldError("password", "Неверный пароль")
            );
        }
    }

    public void ensureMatchConfirmPassword(@NonNull String newPassword, @NonNull String newConfirmPassword) {
        if (!newPassword.equals(newConfirmPassword)) {
            throw new ResourceConflictException(
                    "Incorrect confirm password",
                    new FieldError("confirmPassword", "Пароли не совпадают")
            );
        }
    }

}
