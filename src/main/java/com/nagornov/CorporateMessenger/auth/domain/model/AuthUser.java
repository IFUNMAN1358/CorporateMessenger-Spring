package com.nagornov.CorporateMessenger.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthUser {

    private UUID id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<AuthRole> roles;
    private AuthSession session;

    public static AuthUser initializeUser(AuthUser user) {
        return new AuthUser(
                UUID.randomUUID(),
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                new HashSet<>(),
                null
        );
    }

}
