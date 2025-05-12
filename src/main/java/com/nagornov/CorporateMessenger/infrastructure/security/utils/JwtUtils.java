package com.nagornov.CorporateMessenger.infrastructure.security.utils;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import io.jsonwebtoken.Claims;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class JwtUtils {

    public static JwtAuthentication generateAccessInfo(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setUserId(claims.getSubject());
        jwtInfoToken.setRoles(getRolesFromClaims(claims));
        return jwtInfoToken;
    }

    public static JwtAuthentication generateRefreshInfo(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setUserId(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<Role> getRolesFromClaims(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(roleName -> new Role(null, RoleName.valueOf(roleName), null))
                .collect(Collectors.toSet());
    }

    public static String getTokenFromAuthorizationHeader(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }
        if (!authHeader.startsWith("Bearer ") || authHeader.substring(7).isEmpty()) {
            return null;
        }
        return authHeader.substring(7);
    }

}