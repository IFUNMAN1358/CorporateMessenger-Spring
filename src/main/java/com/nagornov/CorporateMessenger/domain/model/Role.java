package com.nagornov.CorporateMessenger.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Role implements GrantedAuthority {

    private UUID id;
    private String name;
    private LocalDateTime createdAt;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
