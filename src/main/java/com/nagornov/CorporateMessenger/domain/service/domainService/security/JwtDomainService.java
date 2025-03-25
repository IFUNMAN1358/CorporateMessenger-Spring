package com.nagornov.CorporateMessenger.domain.service.domainService.security;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.security.repository.JwtRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtDomainService {

    private final JwtRepository jwtRepository;

    public String generateAccessToken(@NonNull User user, @NonNull List<Role> roles) {
        return jwtRepository.generateAccessToken(
                String.valueOf(user.getId()),
                roles.stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

    public String generateRefreshToken(@NonNull User user) {
        return jwtRepository.generateRefreshToken(
                String.valueOf(user.getId())
        );
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
