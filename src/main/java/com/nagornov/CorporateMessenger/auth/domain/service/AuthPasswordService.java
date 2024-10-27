package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.data.IncorrectPasswordException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthPasswordService {

    private final PasswordEncoder passwordEncoder;

    public AuthUser encodeUserPassword(@NotNull AuthUser user) {
        final String encodedPassword = passwordEncoder.encode(user.getPassword());
        return new AuthUser(
                user.getId(),
                user.getUsername(),
                encodedPassword,
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getRoles(),
                user.getSession()
        );
    }

    public void matchUserPassword(@NotNull String rawPassword, @NotNull String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IncorrectPasswordException();
        }
    }

}
