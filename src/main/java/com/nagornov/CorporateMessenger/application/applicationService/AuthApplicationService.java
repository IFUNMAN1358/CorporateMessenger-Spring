package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaRegistrationKeyDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaRoleDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserRoleDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.redis.RedisSessionDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.JwtDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.security.PasswordDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JpaUserDomainService jpaUserDomainService;
    private final JpaUserRoleDomainService jpaUserRoleDomainService;
    private final JpaRoleDomainService jpaRoleDomainService;
    private final JpaRegistrationKeyDomainService jpaRegistrationKeyDomainService;
    private final RedisSessionDomainService redisSessionDomainService;
    private final PasswordDomainService passwordDomainService;
    private final JwtDomainService jwtDomainService;


    @Transactional
    public JwtResponse registration(RegistrationRequest request) {

        // 1 : Checking key exists by value from form and its status
        RegistrationKey registrationKey = jpaRegistrationKeyDomainService.getByValue(request.getRegistrationKey());
        registrationKey.ensureNotApplied();

        // 2 : Checking user exists by username
        jpaUserDomainService.ensureExistsByUsername(request.getUsername());

        // 3 : Creating user with encoded password
        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                null,
                request.getFirstName(),
                request.getLastName(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        String encodedPassword = passwordDomainService.encodePassword(request.getPassword());
        user.updatePassword(encodedPassword);
        jpaUserDomainService.save(user);

        // 4 : Applying reg key with userId
        jpaRegistrationKeyDomainService.apply(registrationKey, user.getId());

        // 5 : Assigning USER role to the user
        Role userRole = jpaRoleDomainService.getByName(RoleEnum.USER);
        jpaUserRoleDomainService.assignRoleIdToUserId(userRole.getId(), user.getId());

        // 6 : Generating JWT tokens
        String accessToken = jwtDomainService.generateAccessToken(user, List.of(userRole));
        String refreshToken = jwtDomainService.generateRefreshToken(user);

        // 7 : Saving tokens to redis session
        Session session = new Session(
                UUID.randomUUID(),
                accessToken,
                refreshToken,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        redisSessionDomainService.save(user.getId(), session);

        // 8 : Return JWT tokens
        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public JwtResponse login(LoginRequest request) {

        User user = jpaUserDomainService.getByUsername(request.getUsername());

        passwordDomainService.ensureMatchPassword(request.getPassword(), user.getPassword());

        List<Role> userRoles = jpaUserRoleDomainService.findRolesByUserId(user.getId());

        String accessToken = jwtDomainService.generateAccessToken(user, userRoles);
        String refreshToken = jwtDomainService.generateRefreshToken(user);

        Session session = new Session(
                UUID.randomUUID(),
                accessToken,
                refreshToken,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        redisSessionDomainService.save(user.getId(), session);

        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public HttpResponse logout() {

        JwtAuthentication authInfo = jwtDomainService.getAuthInfo();

        User user = jpaUserDomainService.getById(
                UUID.fromString(authInfo.getUserId())
        );

        redisSessionDomainService.deleteByUserId(user.getId());

        return new HttpResponse("Successfully logged out", 200);
    }
}
