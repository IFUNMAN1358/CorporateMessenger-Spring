package com.nagornov.CorporateMessenger.domain.service.domainService.security;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordDomainService {

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
                    List.of(
                            new FieldError("password", "Неверный пароль")
                    )
            );
        }
    }

    public void ensureMatchConfirmPassword(@NonNull String rawPassword, @NonNull String confirmPassword) {
        if (!rawPassword.equals(confirmPassword)) {
            throw new ResourceConflictException(
                    "Incorrect confirm password",
                    List.of(
                            new FieldError("confirmPassword", "Пароли не совпадают")
                    )
            );
        }
    }

}
