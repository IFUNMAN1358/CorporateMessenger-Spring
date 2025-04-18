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
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class JwtRepository {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final Integer jwtAccessExpire;
    private final Integer jwtRefreshExpire;

    public JwtRepository(
            JwtProperties jwtProperties
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getAccessSecret()));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getRefreshSecret()));
        this.jwtAccessExpire = jwtProperties.getAccessExpire();
        this.jwtRefreshExpire = jwtProperties.getRefreshExpire();
    }

    public String generateAccessToken(String userId, Set<String> userRoles) {
        Date currentDate = new Date();
        Date accessExpirationInstant = new Date(currentDate.getTime() + TimeUnit.SECONDS.toMillis(jwtAccessExpire));
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(accessExpirationInstant)
                .signWith(jwtAccessSecret)
                .claim("roles", userRoles)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date currentDate = new Date();
        Date accessExpirationInstant = new Date(currentDate.getTime() + TimeUnit.SECONDS.toMillis(jwtRefreshExpire));
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(accessExpirationInstant)
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