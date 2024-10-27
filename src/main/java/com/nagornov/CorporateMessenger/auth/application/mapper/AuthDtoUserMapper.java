package com.nagornov.CorporateMessenger.auth.application.mapper;

import com.nagornov.CorporateMessenger.auth.application.dto.request.LoginFormRequest;
import com.nagornov.CorporateMessenger.auth.application.dto.request.RegistrationFormRequest;
import com.nagornov.CorporateMessenger.auth.application.dto.response.UserResponse;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthDtoUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "session", ignore = true)
    AuthUser toUser(RegistrationFormRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "session", ignore = true)
    AuthUser toUser(LoginFormRequest request);

    UserResponse toUserResponse(AuthUser user);

}
