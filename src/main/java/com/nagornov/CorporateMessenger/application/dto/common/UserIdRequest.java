package com.nagornov.CorporateMessenger.application.dto.common;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserIdRequest {

    @ValidUuid
    @NotNull
    private String userId;

}
