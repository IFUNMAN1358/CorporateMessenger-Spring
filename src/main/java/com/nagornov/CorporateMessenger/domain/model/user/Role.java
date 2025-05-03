package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Role implements GrantedAuthority {

    private UUID id;
    private RoleName name;
    private Instant createdAt;

    public String getNameAsString() {
        return this.name.toString();
    }

    @Override
    public String getAuthority() {
        return this.name.toString();
    }

}
