package com.nagornov.CorporateMessenger.infrastructure.security.ws;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.infrastructure.security.repository.JwtRepository;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtRepository jwtRepository;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == null || !"CONNECT".equals(accessor.getCommand().toString())) {
            return message;
        }

        List<String> authorizationHeaderContent = accessor.getNativeHeader("Authorization");
        if (authorizationHeaderContent == null || authorizationHeaderContent.isEmpty()) {
            throw new RuntimeException("Authorization header is missing");
        }

        String accessToken = authorizationHeaderContent.getFirst();
        if (!accessToken.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header format");
        }

        accessToken = accessToken.substring(7);

        if (!jwtRepository.validateAccessToken(accessToken)) {
            throw new RuntimeException("Invalid or expired token");
        }

        Claims claims = jwtRepository.getAccessClaims(accessToken);
        JwtAuthentication jwtInfoToken = JwtUtils.generateAccessInfo(claims);
        jwtInfoToken.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);

        return message;
    }
}