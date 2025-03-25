package com.nagornov.CorporateMessenger.infrastructure.security.repository;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Repository
@Slf4j
public class JwtRepository {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final Integer accessExpireMinutes;
    private final Integer refreshExpireDays;

    public JwtRepository(
            JwtProperties jwtProperties
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getAccessSecret()));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getRefreshSecret()));
        this.accessExpireMinutes = jwtProperties.getAccessExpireMinutes();
        this.refreshExpireDays = jwtProperties.getRefreshExpireDays();
    }

    public String generateAccessToken(String userId, Set<String> userRoles) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(accessExpireMinutes).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("roles", userRoles)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(refreshExpireDays).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt");
        } catch (SignatureException sEx) {
            log.error("Invalid signature");
        } catch (Exception e) {
            log.error("invalid token");
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}