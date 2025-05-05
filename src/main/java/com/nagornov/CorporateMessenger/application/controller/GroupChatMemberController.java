package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserResponse;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatMemberApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
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
public class GroupChatMemberController {

    private final GroupChatMemberApplicationService groupChatMemberApplicationService;

    @GetMapping(
            value = "/api/chat/group/{chatId}/members",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<ChatMember>> getGroupChatMembers(@NotNull @PathVariable("chatId") Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        List<ChatMember> chatMembers = groupChatMemberApplicationService.getChatMembers(chatId);
        return ResponseEntity.status(200).body(chatMembers);
    }


    @GetMapping(
            value = "/api/chat/group/{chatId}/members/available",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserResponse>> getAvailableUsersToAdding(@NotNull @PathVariable("chatId") Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        List<UserResponse> usersToAdd = groupChatMemberApplicationService.getAvailableUsersToAdding(chatId);
        return ResponseEntity.status(200).body(usersToAdd);
    }


    @PostMapping(
            value = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> addMembersToGroupChat(
            @NotNull @PathVariable("chatId") Long chatId,
            @RequestBody UserIdsRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) or UserIdsRequest validation error", bindingResult);
        }
        HttpResponse infoResponse = groupChatMemberApplicationService.addMembersToGroupChat(chatId, request);
        return ResponseEntity.status(201).body(infoResponse);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteMembersFromGroupChat(
            @NotNull @PathVariable("chatId") Long chatId,
            @RequestBody UserIdsRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) or UserIdsRequest validation error", bindingResult);
        }
        HttpResponse infoResponse = groupChatMemberApplicationService.deleteMembersFromGroupChat(chatId, request);
        return ResponseEntity.status(200).body(infoResponse);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/leave",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> leaveFromGroupChat(@NotNull @PathVariable("chatId") Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        HttpResponse infoResponse = groupChatMemberApplicationService.leaveFromGroupChat(chatId);
        return ResponseEntity.status(200).body(infoResponse);
    }

}
