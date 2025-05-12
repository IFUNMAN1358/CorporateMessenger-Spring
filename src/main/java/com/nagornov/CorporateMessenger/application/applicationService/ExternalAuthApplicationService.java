package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.auth.JwtResponse;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.auth.PasswordService;
import com.nagornov.CorporateMessenger.domain.service.user.RoleService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExternalAuthApplicationService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final RoleService roleService;
    private final JwtService jwtService;


    public JwtResponse externalLogin(@NonNull String username, @NonNull String password) {
        User user = userService.getByUsername(username);

        userService.validateUserNotDeleted(user);

        passwordService.ensureMatchEncodedPassword(password, user.getPassword());

        List<Role> roles = roleService.findAllByUserId(user.getId());

        String accessToken = jwtService.generateAccessToken(user, roles);
        String refreshToken = jwtService.generateRefreshToken(user);

//        sessionService.saveToRedis(
//                user.getId(),
//                accessToken,
//                refreshToken,
//                csrfToken.getToken(),
//                jwtProperties.getRefreshExpire(),
//                TimeUnit.SECONDS
//        );

        return new JwtResponse(accessToken, refreshToken);
    }
}