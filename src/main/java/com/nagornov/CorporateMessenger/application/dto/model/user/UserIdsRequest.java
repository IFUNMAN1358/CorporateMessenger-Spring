package com.nagornov.CorporateMessenger.application.dto.model.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserIdsRequest {

    @NotEmpty
    private List<String> userIds;

}
