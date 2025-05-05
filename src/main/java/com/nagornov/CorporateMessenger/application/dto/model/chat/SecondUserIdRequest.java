package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class SecondUserIdRequest {

    @NotNull
    @NotBlank
    @Size
    @ValidUuid
    private String secondUserId;

    //
    //
    //

    public UUID getSecondUserIdAsUUID() {
        return UUID.fromString(secondUserId);
    }

}
