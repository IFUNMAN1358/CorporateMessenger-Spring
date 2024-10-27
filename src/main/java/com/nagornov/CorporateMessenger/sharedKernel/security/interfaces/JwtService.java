package com.nagornov.CorporateMessenger.sharedKernel.security.interfaces;

import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.sharedKernel.security.model.JwtAuthentication;

public interface JwtService {

    String generateAccessToken(SecurityUser user);
    String generateRefreshToken(SecurityUser user);

    JwtAuthentication getAuthInfo();

}
