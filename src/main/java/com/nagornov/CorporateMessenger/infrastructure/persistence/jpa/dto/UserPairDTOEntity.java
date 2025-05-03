package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UserPairDTOEntity implements Serializable {

    private JpaUserEntity user1;
    private JpaUserEntity user2;

}
