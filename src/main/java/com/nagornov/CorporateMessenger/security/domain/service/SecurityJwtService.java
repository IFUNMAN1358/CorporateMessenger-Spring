package com.nagornov.CorporateMessenger.security.domain.service;

import com.nagornov.CorporateMessenger.sharedKernel.security.model.JwtAuthentication;
import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.infrastructure.repository.JwtRepository;
import com.nagornov.CorporateMessenger.security.infrastructure.util.JwtUtils;
import com.nagornov.CorporateMessenger.sharedKernel.security.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityJwtService implements JwtService {

    private final JwtRepository jwtRepository;

    @Override
    public String generateAccessToken(SecurityUser user) {
        return jwtRepository.generateAccessToken(
                String.valueOf(user.getId()),
                JwtUtils.convertRoleToString(user.getRoles())
        );
    }

    @Override
    public String generateRefreshToken(SecurityUser user) {
        return jwtRepository.generateRefreshToken(
                String.valueOf(user.getId())
        );
    }

    @Override
    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
