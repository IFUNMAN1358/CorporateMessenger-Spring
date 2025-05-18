package com.nagornov.CorporateMessenger.application.dto.auth;

import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ExternalAuthResponse {

    private String accessToken;
    private String refreshToken;
    private String username;
    private String firstName;
    private String lastName;
    private Set<String> userRoles;

    public ExternalAuthResponse(
            @NonNull String accessToken,
            @NonNull String refreshToken,
            @NonNull User user,
            @NonNull List<Role> userRoles
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();

        this.userRoles = userRoles.stream().map(Role::getNameAsString).collect(Collectors.toSet());
    }

}
