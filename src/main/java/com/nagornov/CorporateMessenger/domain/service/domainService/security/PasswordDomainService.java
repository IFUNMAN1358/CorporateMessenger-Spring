package com.nagornov.CorporateMessenger.domain.service.domainService.security;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordDomainService {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(@NonNull String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchPassword(@NonNull String rawPassword, @NonNull String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void ensureMatchPassword(@NonNull String rawPassword, @NonNull String encodedPassword) {
        if (!matchPassword(rawPassword, encodedPassword)) {
            throw new ResourceConflictException("Incorrect password");
        }
    }

}
