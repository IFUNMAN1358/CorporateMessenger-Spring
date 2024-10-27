package com.nagornov.CorporateMessenger.security.infrastructure.util;

import com.nagornov.CorporateMessenger.security.domain.model.SecurityRole;
import com.nagornov.CorporateMessenger.sharedKernel.security.model.JwtAuthentication;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class JwtUtils {

    public static JwtAuthentication generateAccessInfo(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setUserId(claims.getSubject());
        jwtInfoToken.setRoles(convertStringToRole(claims));
        return jwtInfoToken;
    }

    public static JwtAuthentication generateRefreshInfo(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setUserId(claims.getSubject());
        return jwtInfoToken;
    }

    public static Set<String> convertRoleToString(Set<SecurityRole> roles) {
        return roles.stream()
                .map(SecurityRole::getName)
                .collect(Collectors.toSet());
    }

    private static Set<SecurityRole> convertStringToRole(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(roleName -> new SecurityRole(null, roleName))
                .collect(Collectors.toSet());
    }

    public static String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

}