package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.InformationalResponse;
import com.nagornov.CorporateMessenger.application.dto.common.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponse;
import com.nagornov.CorporateMessenger.application.service.GroupChatMemberApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.model.GroupChatMember;
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
    ResponseEntity<List<GroupChatMember>> getGroupChatMembers(@ValidUuid @PathVariable("chatId") String chatId) {
        List<GroupChatMember> groupChatMembers =
                groupChatMemberApplicationService.getGroupChatMembers(chatId);
        return ResponseEntity.status(200).body(groupChatMembers);
    }


    @GetMapping(
            value = "/api/chat/group/{chatId}/members/available",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserResponse>> getAvailableUsersToAdd(@ValidUuid @PathVariable("chatId") String chatId) {
        List<UserResponse> usersToAdd =
                groupChatMemberApplicationService.getAvailableUsersToAdd(chatId);
        return ResponseEntity.status(200).body(usersToAdd);
    }


    @PostMapping(
            value = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> addMembers(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UserIdsRequest request
    ) {
        InformationalResponse infoResponse =
                groupChatMemberApplicationService.addMembers(chatId, request);
        return ResponseEntity.status(200).body(infoResponse);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> deleteMembers(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UserIdsRequest request
    ) {
        InformationalResponse infoResponse =
                groupChatMemberApplicationService.deleteMembers(chatId, request);
        return ResponseEntity.status(200).body(infoResponse);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/leave",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> leaveFromChat(@ValidUuid @PathVariable("chatId") String chatId) {
        InformationalResponse infoResponse =
                groupChatMemberApplicationService.leaveFromChat(chatId);
        return ResponseEntity.status(200).body(infoResponse);
    }

}
