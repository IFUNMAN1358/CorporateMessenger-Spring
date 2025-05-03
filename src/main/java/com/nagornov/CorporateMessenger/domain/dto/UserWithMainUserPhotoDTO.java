package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithMainUserPhotoDTO {

    private User user;
    private UserPhoto mainUserPhoto;

}
