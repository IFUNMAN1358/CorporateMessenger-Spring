package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UserIdRequest {

    @NotNull
    @NotBlank
    @Size
    @ValidUuid
    private String userId;

    //
    //
    //

    public UUID getUserIdAsUUID() {
        return UUID.fromString(userId);
    }

}
