package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactPairDTO {

    private Contact contact1;
    private Contact contact2;

}
