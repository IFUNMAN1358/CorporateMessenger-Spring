package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.model.user.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.applicationService.ChatMemberApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserWithUserPhotoResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class ChatMemberController {

    private final ChatMemberApplicationService chatMemberApplicationService;

    @GetMapping(
            path = "/api/chat/group/{chatId}/members",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserWithUserPhotoResponse>> getGroupChatMembers(
            @NotNull @PathVariable("chatId") Long chatId,
            @RequestParam int page,
            @RequestParam int size,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        List<UserWithUserPhotoResponse> response = chatMemberApplicationService.getGroupChatMembers(chatId, page, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chat/group/{chatId}/members/available",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserWithUserPhotoResponse>> getAvailableUsersToAdding(
            @NotNull @PathVariable("chatId") Long chatId,
            @RequestParam int page,
            @RequestParam int size,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        List<UserWithUserPhotoResponse> response = chatMemberApplicationService.getAvailableUsersToAdding(chatId, page, size);
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            path = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> addMembersToGroupChat(
            @NotNull @PathVariable("chatId") Long chatId,
            @RequestBody UserIdsRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) or UserIdsRequest validation error", bindingResult);
        }
        chatMemberApplicationService.addMembersToGroupChat(chatId, request);
        return ResponseEntity.status(201).body("Member added");
    }


    @DeleteMapping(
            path = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteMembersFromGroupChat(
            @NotNull @PathVariable("chatId") Long chatId,
            @RequestBody UserIdsRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) or UserIdsRequest validation error", bindingResult);
        }
        chatMemberApplicationService.deleteMembersFromGroupChat(chatId, request);
        return ResponseEntity.status(200).body("Member deleted");
    }


    @DeleteMapping(
            path = "/api/chat/group/{chatId}/leave",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> leaveFromGroupChat(@NotNull @PathVariable("chatId") Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        chatMemberApplicationService.leaveFromGroupChat(chatId);
        return ResponseEntity.status(200).body("You left from group chat");
    }

}
