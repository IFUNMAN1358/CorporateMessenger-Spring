package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponse;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatMemberApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class GroupChatMemberController {

    private final GroupChatMemberApplicationService groupChatMemberApplicationService;

    @GetMapping(
            value = "/api/chat/group/{chatId}/members",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<ChatMember>> getGroupChatMembers(@ValidUuid @PathVariable("chatId") String chatId) {
        List<ChatMember> chatMembers =
                groupChatMemberApplicationService.getGroupChatMembers(chatId);
        return ResponseEntity.status(200).body(chatMembers);
    }


    @GetMapping(
            value = "/api/chat/group/{chatId}/members/available",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserResponse>> getAvailableUsersToAdding(@ValidUuid @PathVariable("chatId") String chatId) {
        List<UserResponse> usersToAdd =
                groupChatMemberApplicationService.getAvailableUsersToAdding(chatId);
        return ResponseEntity.status(200).body(usersToAdd);
    }


    @PostMapping(
            value = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> addMembersToGroupChat(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UserIdsRequest request
    ) {
        HttpResponse infoResponse =
                groupChatMemberApplicationService.addMembersToGroupChat(chatId, request);
        return ResponseEntity.status(201).body(infoResponse);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteMembersFromGroupChat(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UserIdsRequest request
    ) {
        HttpResponse infoResponse =
                groupChatMemberApplicationService.deleteMembersFromGroupChat(chatId, request);
        return ResponseEntity.status(200).body(infoResponse);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/leave",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> leaveFromGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        HttpResponse infoResponse =
                groupChatMemberApplicationService.leaveFromGroupChat(chatId);
        return ResponseEntity.status(200).body(infoResponse);
    }

}
