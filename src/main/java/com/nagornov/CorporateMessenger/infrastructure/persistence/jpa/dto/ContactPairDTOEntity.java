package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaContactEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ContactPairDTOEntity implements Serializable {

    private JpaContactEntity contact1;
    private JpaContactEntity contact2;

}
