package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.auth.CsrfService;
import com.nagornov.CorporateMessenger.domain.service.auth.SessionService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.auth.PasswordService;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final RoleService roleService;
    private final RegistrationKeyService registrationKeyService;
    private final EmployeeService employeeService;
    private final SessionService sessionService;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final CsrfService csrfService;


    @Transactional
    public JwtResponse registration(
            @NonNull HttpServletRequest servletReq,
            @NonNull HttpServletResponse servletRes,
            @NonNull RegistrationRequest request
    ) {

        passwordService.ensureMatchConfirmPassword(request.getPassword(), request.getConfirmPassword());

        RegistrationKey registrationKey = registrationKeyService.getByValue(request.getRegistrationKey());
        registrationKey.ensureNotApplied();

        userService.ensureNotExistsByUsername(request.getUsername());

        String encodedPassword = passwordService.encodePassword(request.getPassword());
        User user = userService.create(
                request.getUsername(),
                encodedPassword,
                request.getFirstName(),
                request.getLastName()
        );

        registrationKeyService.apply(registrationKey, user.getId());

        Role role = roleService.assignRoleToUserId(user.getId(), RoleName.ROLE_USER);

        userSettingsService.create(user.getId());
        employeeService.create(user.getId());

        CsrfToken csrfToken = csrfService.generateToken(servletReq);
        String accessToken = jwtService.generateAccessToken(user, List.of(role));
        String refreshToken = jwtService.generateRefreshToken(user);

        sessionService.saveToRedis(
                user.getId(),
                accessToken,
                refreshToken,
                csrfToken.getToken(),
                jwtProperties.getRefreshExpire(),
                TimeUnit.SECONDS
        );

        csrfService.saveToken(csrfToken, servletReq, servletRes); // save csrf token in cookie
        return new JwtResponse(accessToken, refreshToken); // return jwt response
    }


    @Transactional
    public JwtResponse login(
            @NonNull HttpServletRequest servletReq,
            @NonNull HttpServletResponse servletRes,
            @NonNull LoginRequest request
    ) {

        User user = userService.getByUsername(request.getUsername());

        userService.validateUserNotDeleted(user);

        passwordService.ensureMatchEncodedPassword(request.getPassword(), user.getPassword());

        List<Role> roles = roleService.findAllByUserId(user.getId());

        CsrfToken csrfToken = csrfService.generateToken(servletReq);
        String accessToken = jwtService.generateAccessToken(user, roles);
        String refreshToken = jwtService.generateRefreshToken(user);

        sessionService.saveToRedis(
                user.getId(),
                accessToken,
                refreshToken,
                csrfToken.getToken(),
                jwtProperties.getRefreshExpire(),
                TimeUnit.SECONDS
        );

        csrfService.saveToken(csrfToken, servletReq, servletRes); // save csrf token in cookie
        return new JwtResponse(accessToken, refreshToken); // return jwt response
    }


    @Transactional
    public void logout() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        sessionService.deleteFromRedis(user.getId());
    }
}
