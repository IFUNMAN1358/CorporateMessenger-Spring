package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUsername;
import com.nagornov.CorporateMessenger.domain.annotation.enums.UsernameType;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUsernameRequest {

    @ValidUsername(usernameType = UsernameType.USER)
    @Size(message = "Имя пользователя должно содержать от 5 до 32 символов", min = 5, max = 32)
    private String username;

}
