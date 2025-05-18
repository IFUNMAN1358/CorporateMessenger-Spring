package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.NotificationApplicationService;
import com.nagornov.CorporateMessenger.domain.model.user.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationApplicationService notificationApplicationService;


    @GetMapping(
            path = "/api/user/notifications",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam String category,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<Notification> response = notificationApplicationService.getNotifications(category, page, size);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/notification/{notificationId}/process",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Notification> processNotificationByNotId(
            @PathVariable UUID notificationId
    ) {
        Notification response = notificationApplicationService.processNotificationByNotId(notificationId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/notification/{notificationId}/read",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Notification> readNotificationByNotId(
            @PathVariable UUID notificationId
    ) {
        Notification response = notificationApplicationService.readNotificationByNotId(notificationId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/notifications/read-all",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> readAllNotifications() {
        notificationApplicationService.readAllNotifications();
        return ResponseEntity.status(204).body("Notifications success read");
    }


    @DeleteMapping(
            path = "/api/user/notification/{notificationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteNotificationByNotId(
            @PathVariable UUID notificationId
    ) {
        notificationApplicationService.deleteNotificationByNotId(notificationId);
        return ResponseEntity.status(204).body("Notification deleted");
    }


    @DeleteMapping(
            path = "/api/user/notifications",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteAllNotifications() {
        notificationApplicationService.deleteAllNotifications();
        return ResponseEntity.status(204).body("All notifications deleted");
    }
}