package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtSession;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaRegistrationKeyDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaRoleDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserRoleDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisJwtSessionDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.PasswordDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserRoleDomainService jpaUserRoleDomainService;
    private final JpaRoleDomainService jpaRoleDomainService;
    private final JpaRegistrationKeyDomainService jpaRegistrationKeyDomainService;
    private final RedisJwtSessionDomainService redisJwtSessionDomainService;
    private final PasswordDomainService passwordDomainService;
    private final JwtDomainService jwtDomainService;


    @Transactional
    public JwtResponse registration(RegistrationRequest request) {

        passwordDomainService.ensureMatchConfirmPassword(request.getPassword(), request.getConfirmPassword());

        RegistrationKey registrationKey = jpaRegistrationKeyDomainService.getByValue(request.getRegistrationKey());
        registrationKey.ensureNotApplied();

        jpaUserDomainService.ensureNotExistsByUsername(request.getUsername());

        String encodedPassword = passwordDomainService.encodePassword(request.getPassword());
        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                encodedPassword,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        jpaUserDomainService.save(user);

        jpaRegistrationKeyDomainService.apply(registrationKey, user.getId());

        Role role = jpaRoleDomainService.getByName(RoleEnum.USER);
        jpaUserRoleDomainService.assignRoleToUser(role.getId(), user.getId());

        String accessToken = jwtDomainService.generateAccessToken(user, List.of(role));
        String refreshToken = jwtDomainService.generateRefreshToken(user);

        JwtSession jwtSession = new JwtSession(
                accessToken,
                refreshToken,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        redisJwtSessionDomainService.saveByKeyExpire(user.getId(), jwtSession, 2, TimeUnit.DAYS);

        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public JwtResponse login(LoginRequest request) {

        User user = jpaUserDomainService.getByUsername(request.getUsername());

        passwordDomainService.ensureMatchEncodedPassword(request.getPassword(), user.getPassword());

        List<Role> userRoles = jpaUserRoleDomainService.findRolesByUserId(user.getId());

        String accessToken = jwtDomainService.generateAccessToken(user, userRoles);
        String refreshToken = jwtDomainService.generateRefreshToken(user);

        JwtSession jwtSession = new JwtSession(
                accessToken,
                refreshToken,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        redisJwtSessionDomainService.saveByKeyExpire(user.getId(), jwtSession, 2, TimeUnit.DAYS);

        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public HttpResponse logout() {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        User user = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        redisJwtSessionDomainService.deleteByKey(user.getId());

        return new HttpResponse("Successfully logged out", 200);
    }
}
