package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserIdsRequest {

    @NotEmpty
    private List<@ValidUuid String> userIds;

}
