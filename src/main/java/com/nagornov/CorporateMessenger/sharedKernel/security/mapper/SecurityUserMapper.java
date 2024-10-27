package com.nagornov.CorporateMessenger.sharedKernel.security.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SecurityUserMapper {

    SecurityUser toSecurityUser(AuthUser authUser);

}
