package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.NotificationApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.notification.NotificationIdRequest;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import com.nagornov.CorporateMessenger.domain.model.user.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(name = "category", defaultValue = "all") String category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("RequestParam(category) | RequestParam(page) | RequestParam(pageSize) validation error", bindingResult);
        }
        Page<Notification> response = notificationApplicationService.getNotifications(category, page, pageSize);
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
        Notification response = notificationApplicationService.processNotification(request);
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
        Notification response = notificationApplicationService.readNotification(request);
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
            path = "/api/user/notification",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteNotification(@RequestBody NotificationIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("NotificationIdRequest validation error", bindingResult);
        }
        notificationApplicationService.deleteNotification(request);
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