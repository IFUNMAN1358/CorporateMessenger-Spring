package com.nagornov.CorporateMessenger.security.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class SecurityRole implements GrantedAuthority {

    private UUID id;
    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
