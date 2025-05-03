package com.nagornov.CorporateMessenger.domain.enums.model;

public enum NotificationType {

    // ACCOUNT category

    // CHAT category

    // CONTACT category
    REQUEST_TO_JOIN_CONTACT,  // Заявка на добавление в ваши контакты
    JOIN_CONTACT,             // Пользователь добавился в ваши контакты
    CONFIRM_CONTACT_REQUEST,   // Пользователь принял вашу заявку в его контакты
    REJECT_CONTACT_REQUEST;   // Пользователь отклонил вашу заявку в его контакты

}
