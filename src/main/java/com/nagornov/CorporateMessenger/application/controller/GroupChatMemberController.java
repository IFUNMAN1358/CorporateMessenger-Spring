package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.common.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.user.UserResponse;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatMemberApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
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
    private final ControllerLogger controllerLogger;

    @GetMapping(
            value = "/api/chat/group/{chatId}/members",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<GroupChatMember>> getGroupChatMembers(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Get group chat members started");
            final List<GroupChatMember> groupChatMembers =
                    groupChatMemberApplicationService.getGroupChatMembers(chatId);
            controllerLogger.info("Get group chat members finished");
            return ResponseEntity.status(200).body(groupChatMembers);
        } catch (Exception e) {
            controllerLogger.error("Get group chat members failed", e);
            throw e;
        }
    }


    @GetMapping(
            value = "/api/chat/group/{chatId}/members/available",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<UserResponse>> getAvailableUsersToAdding(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Get available users to add to the group chat started");
            final List<UserResponse> usersToAdd =
                    groupChatMemberApplicationService.getAvailableUsersToAdding(chatId);
            controllerLogger.info("Get available users to add to the group chat finished");
            return ResponseEntity.status(200).body(usersToAdd);
        } catch (Exception e) {
            controllerLogger.error("Get available users to add to the group chat failed", e);
            throw e;
        }
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
        try {
            controllerLogger.info("Add members to the group chat started");
            final HttpResponse infoResponse =
                    groupChatMemberApplicationService.addMembersToGroupChat(chatId, request);
            controllerLogger.info("Add members to the group chat finished");
            return ResponseEntity.status(201).body(infoResponse);
        } catch (Exception e) {
            controllerLogger.error("Add members to the group chat failed", e);
            throw e;
        }
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
        try {
            controllerLogger.info("Delete members from the group chat started");
            final HttpResponse infoResponse =
                    groupChatMemberApplicationService.deleteMembersFromGroupChat(chatId, request);
            controllerLogger.info("Delete members from the group chat finished");
            return ResponseEntity.status(200).body(infoResponse);
        } catch (Exception e) {
            controllerLogger.error("Delete members from the group chat failed", e);
            throw e;
        }
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/leave",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> leaveFromGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Leave from the group chat started");
            final HttpResponse infoResponse =
                    groupChatMemberApplicationService.leaveFromGroupChat(chatId);
            controllerLogger.info("Leave from the group chat finished");
            return ResponseEntity.status(200).body(infoResponse);
        } catch (Exception e) {
            controllerLogger.error("Leave from the group chat failed", e);
            throw e;
        }
    }

}
