package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.ContactApplicationService;
import com.nagornov.CorporateMessenger.application.dto.user.UserIdRequest;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private final ContactApplicationService contactApplicationService;


    @PostMapping(
            path = "/api/user/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Contact> addContactByUserId(@Validated @RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest form validation error", bindingResult);
        }
        Contact response = contactApplicationService.addContactByUserId(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/user/{userId}/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Optional<Contact>> findContactByUserId(@Validated @PathVariable @NotNull @NotBlank UUID userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("userId path variable validation error", bindingResult);
        }
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
    ResponseEntity<List<Contact>> getContactsByUserId(@Validated @PathVariable @NotNull @NotBlank UUID userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("userId path variable validation error", bindingResult);
        }
        List<Contact> response = contactApplicationService.getContactsByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/user/contact/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> confirmContactByUserId(@Validated @RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest form validation error", bindingResult);
        }
        contactApplicationService.confirmContactByUserId(request);
        return ResponseEntity.status(200).body("Contact confirmed");
    }


    @PatchMapping(
            path = "/api/user/contact/reject",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> rejectContactByUserId(@Validated @RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest form validation error", bindingResult);
        }
        contactApplicationService.rejectContactByUserId(request);
        return ResponseEntity.status(200).body("Contact rejected");
    }


    @DeleteMapping(
            path = "/api/user/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteContactByUserId(@Validated @RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest form validation error", bindingResult);
        }
        contactApplicationService.deleteContactByUserId(request);
        return ResponseEntity.status(204).body("Contact deleted");
    }
}
