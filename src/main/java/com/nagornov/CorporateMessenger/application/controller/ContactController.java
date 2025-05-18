package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.ContactApplicationService;
import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class ContactController {

    private final ContactApplicationService contactApplicationService;


    @PostMapping(
            path = "/api/user/{userId}/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Contact> addContactByUserId(
            @PathVariable UUID userId
    ) {
        Contact response = contactApplicationService.addContactByUserId(userId);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Optional<Contact>> findContactByUserId(
            @PathVariable UUID userId
    ) {
        Optional<Contact> response = contactApplicationService.findContactByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<Contact>> getMyContacts() {
        List<Contact> response = contactApplicationService.getMyContacts();
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<Contact>> getContactsByUserId(
            @PathVariable UUID userId
    ) {
        List<Contact> response = contactApplicationService.getContactsByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/{userId}/contact/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> confirmContactByUserId(
            @PathVariable UUID userId
    ) {
        contactApplicationService.confirmContactByUserId(userId);
        return ResponseEntity.status(200).body("Contact confirmed");
    }


    @PatchMapping(
            path = "/api/user/{userId}/contact/reject",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> rejectContactByUserId(
            @PathVariable UUID userId
    ) {
        contactApplicationService.rejectContactByUserId(userId);
        return ResponseEntity.status(200).body("Contact rejected");
    }


    @DeleteMapping(
            path = "/api/user/{userId}/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteContactByUserId(
            @PathVariable UUID userId
    ) {
        contactApplicationService.deleteContactByUserId(userId);
        return ResponseEntity.status(204).body("Contact deleted");
    }
}
