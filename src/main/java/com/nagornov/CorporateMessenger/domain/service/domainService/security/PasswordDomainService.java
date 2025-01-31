package com.nagornov.CorporateMessenger.domain.service.domainService.security;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordDomainService {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(@NotNull String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void matchPassword(@NotNull String rawPassword, @NotNull String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ResourceConflictException("Invalid password");
        }
    }

}
