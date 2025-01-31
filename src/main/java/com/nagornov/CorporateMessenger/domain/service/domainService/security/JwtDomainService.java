package com.nagornov.CorporateMessenger.domain.service.domainService.security;

import com.nagornov.CorporateMessenger.domain.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.infrastructure.security.repository.JwtRepository;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.JwtUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtDomainService {

    private final JwtRepository jwtRepository;

    public String generateAccessToken(@NotNull User user) {
        return jwtRepository.generateAccessToken(
                String.valueOf(user.getId()),
                JwtUtils.convertRoleToString(user.getRoles())
        );
    }

    public String generateRefreshToken(@NotNull User user) {
        return jwtRepository.generateRefreshToken(
                String.valueOf(user.getId())
        );
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
