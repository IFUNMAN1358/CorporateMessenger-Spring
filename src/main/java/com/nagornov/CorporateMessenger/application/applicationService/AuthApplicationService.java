package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.application.dto.auth.LoginRequest;
import com.nagornov.CorporateMessenger.application.dto.auth.RegistrationRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.auth.CsrfService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtSessionService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.auth.PasswordService;
import com.nagornov.CorporateMessenger.domain.service.user.*;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.CsrfProperties;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final CsrfProperties csrfProperties;
    private final JwtProperties jwtProperties;
    //
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final RoleService roleService;
    private final RegistrationKeyService registrationKeyService;
    private final EmployeeService employeeService;
    private final JwtSessionService jwtSessionService;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final CsrfService csrfService;


    public HttpResponse getCsrfToken(HttpServletRequest request, HttpServletResponse response) {

        CsrfToken currentCsrfToken = csrfService.loadToken(request);
        CsrfToken newCsrfToken = csrfService.generateToken(request);

        // If token not exists
        if (currentCsrfToken == null || !csrfService.existsInRedis(currentCsrfToken.getToken())) {

            csrfService.saveToken(newCsrfToken, request, response);
            csrfService.saveToRedis(
                    newCsrfToken.getToken(),
                    csrfProperties.getCookie().getMaxAge(),
                    TimeUnit.SECONDS
            );

        // If token exists
        } else {

            csrfService.saveToken(newCsrfToken, request, response);
            csrfService.deleteFromRedis(currentCsrfToken.getToken());
            csrfService.saveToRedis(
                    newCsrfToken.getToken(),
                    csrfProperties.getCookie().getMaxAge(),
                    TimeUnit.SECONDS
            );

        }

        return new HttpResponse("Csrf token successfully generated and saved in cookie", 200);
    }


    @Transactional
    public JwtResponse registration(RegistrationRequest request) {

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

        String accessToken = jwtService.generateAccessToken(user, List.of(role));
        String refreshToken = jwtService.generateRefreshToken(user);

        jwtSessionService.saveToRedis(user.getId(), accessToken, refreshToken, jwtProperties.getRefreshExpire(), TimeUnit.SECONDS);

        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public JwtResponse login(LoginRequest request) {

        User user = userService.getByUsername(request.getUsername());

        userService.validateUserNotDeleted(user);

        passwordService.ensureMatchEncodedPassword(request.getPassword(), user.getPassword());

        List<Role> roles = roleService.findAllByUserId(user.getId());

        String accessToken = jwtService.generateAccessToken(user, roles);
        String refreshToken = jwtService.generateRefreshToken(user);

        jwtSessionService.saveToRedis(user.getId(), accessToken, refreshToken, jwtProperties.getRefreshExpire(), TimeUnit.SECONDS);

        return new JwtResponse(accessToken, refreshToken);
    }


    @Transactional
    public HttpResponse logout() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        jwtSessionService.deleteFromRedis(user.getId());

        return new HttpResponse("Successfully logged out", 200);
    }
}
