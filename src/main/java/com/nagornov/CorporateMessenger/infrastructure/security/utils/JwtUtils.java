package com.nagornov.CorporateMessenger.infrastructure.security.utils;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.StringUtils;

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

    //
    //
    //

    private static Set<Role> getRolesFromClaims(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(roleName -> new Role(null, RoleName.valueOf(roleName), null))
                .collect(Collectors.toSet());
    }

    //
    //
    //

    public static String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public static String getTokenFromRequest(ServerHttpRequest request) {
        List<String> authHeader = request.getHeaders().get("Authorization");
        if (authHeader != null && !authHeader.isEmpty()) {
            String bearerToken = authHeader.get(0);
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

}