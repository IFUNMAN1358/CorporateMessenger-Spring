package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.enums.model.ContactsVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.EmployeeVisibility;
import com.nagornov.CorporateMessenger.domain.enums.model.ProfileVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSettingsRequest {

    @NotNull(message = "Настройка 'Подтверждать заявки в контакты' не может быть null")
    private Boolean isConfirmContactRequests;

    @NotNull(message = "Настройка 'Видимость контактов' не может быть null")
    private ContactsVisibility contactsVisibility; // EVERYONE, CONTACTS, ONLY_ME;

    @NotNull(message = "Настройка 'Видимость данных профиля' не может быть null")
    private ProfileVisibility profileVisibility; // EVERYONE, CONTACTS, ONLY_ME;

    @NotNull(message = "Настройка 'Видимость данных сотрудника' не может быть null")
    private EmployeeVisibility employeeVisibility; // EVERYONE, CONTACTS, ONLY_ME;

    @NotNull(message = "Настройка 'Отображение аккаунта в поиске' не может быть null")
    private Boolean isSearchable;

}
