package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.domain.enums.model.NotificationCategory;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.Notification;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.user.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationApplicationService {

    private final JwtService jwtService;
    private final NotificationService notificationService;


    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(@NonNull String category, int page, int size) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        NotificationCategory enumCategory = notificationService.defineCategoryFromString(category);

        if (enumCategory.equals(NotificationCategory.ALL)) {
            return notificationService.findAllByUserId(authInfo.getUserIdAsUUID(), page, size);
        }
        return notificationService.findAllByUserIdAndCategory(authInfo.getUserIdAsUUID(), enumCategory, page, size);
    }


    @Transactional
    public Notification processNotificationByNotId(@NonNull UUID notificationId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Notification notification = notificationService.getByIdAndUserId(notificationId, authInfo.getUserIdAsUUID());

        if (notification.getIsProcessed()) {
            throw new ResourceConflictException("Notification is already processed");
        }
        notification.markAsProcessed();
        return notificationService.update(notification);
    }


    @Transactional
    public Notification readNotificationByNotId(@NonNull UUID notificationId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Notification notification = notificationService.getByIdAndUserId(notificationId, authInfo.getUserIdAsUUID());

        if (notification.getIsRead()) {
            throw new ResourceConflictException("Notification is already read");
        }
        notification.markAsRead();
        return notificationService.update(notification);
    }


    @Transactional
    public void readAllNotifications() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        notificationService.readAllByUserId(authInfo.getUserIdAsUUID());
    }


    @Transactional
    public void deleteNotificationByNotId(@NonNull UUID notificationId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        notificationService.deleteByIdAndUserId(notificationId, authInfo.getUserIdAsUUID());
    }

    @Transactional
    public void deleteAllNotifications() {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        notificationService.deleteAllByUserId(authInfo.getUserIdAsUUID());
    }
}