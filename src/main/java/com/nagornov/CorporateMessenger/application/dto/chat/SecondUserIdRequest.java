package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SecondUserIdRequest {

    @NotNull
    @NotBlank
    @ValidUuid
    private String secondUserId;

    public UUID getSecondUserIdAsUUID() {
        return UUID.fromString(this.secondUserId);
    }

}
