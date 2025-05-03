package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.ApplicationNotificationService;
import com.nagornov.CorporateMessenger.application.dto.user.NotificationIdRequest;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import com.nagornov.CorporateMessenger.domain.model.user.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final ApplicationNotificationService applicationNotificationService;

    @GetMapping(
            path = "/api/user/notifications",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(name = "category", defaultValue = "all") String category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize
    ) {
        Page<Notification> response = applicationNotificationService.getNotifications(category, page, pageSize);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/notification/process",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Notification> processNotification(@RequestBody NotificationIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("NotificationIdRequest validation error", bindingResult);
        }
        Notification response = applicationNotificationService.processNotification(request);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/notification/read",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Notification> readNotification(@RequestBody NotificationIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("NotificationIdRequest validation error", bindingResult);
        }
        Notification response = applicationNotificationService.readNotification(request);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/notifications/read-all",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> readAllNotifications() {
        applicationNotificationService.readAllNotifications();
        return ResponseEntity.status(204).body("Notifications success read");
    }


    @DeleteMapping(
            path = "/api/user/notification",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteNotification(@RequestBody NotificationIdRequest request) {
        applicationNotificationService.deleteNotification(request);
        return ResponseEntity.status(204).body("Notification deleted");
    }


    @DeleteMapping(
            path = "/api/user/notifications",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteAllNotifications() {
        applicationNotificationService.deleteAllNotifications();
        return ResponseEntity.status(204).body("All notifications deleted");
    }
}