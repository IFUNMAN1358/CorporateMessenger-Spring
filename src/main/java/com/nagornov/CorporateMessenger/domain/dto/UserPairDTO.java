package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPairDTO {

    private User user1;
    private User user2;

}
