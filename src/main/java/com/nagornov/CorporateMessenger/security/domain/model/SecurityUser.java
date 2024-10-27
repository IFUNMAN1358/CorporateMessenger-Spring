package com.nagornov.CorporateMessenger.security.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class SecurityUser {

    private UUID id;
    private String username;
    private String password;
    private Set<SecurityRole> roles;
    private SecuritySession session;

}
