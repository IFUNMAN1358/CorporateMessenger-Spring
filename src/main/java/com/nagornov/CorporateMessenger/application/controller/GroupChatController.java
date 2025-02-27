package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.UpdateGroupChatMetadataRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.GroupChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.common.UserIdRequest;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupChatController {

    private final GroupChatApplicationService groupChatApplicationService;
    private final ControllerLogger controllerLogger;

    @PostMapping(
            value = "/api/chat/group",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> createGroupChat(@Validated @ModelAttribute CreateGroupChatRequest request) {
        try {
            controllerLogger.info("Create group chat started");
            final HttpResponse response =
                    groupChatApplicationService.createGroupChat(request);
            controllerLogger.info("Create group chat finished");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            controllerLogger.error("Create group chat failed", e);
            throw e;
        }
    }


    @GetMapping(
            value = "/api/chat/group/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<GroupChatSummaryResponse>> getAllGroupChats() {
        try {
            controllerLogger.info("Get all group chats started");
            final List<GroupChatSummaryResponse> response =
                    groupChatApplicationService.getAllGroupChats();
            controllerLogger.info("Get all group chats finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get all group chats failed", e);
            throw e;
        }
    }


    @GetMapping(
            value = "/api/chat/group/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<GroupChat> getGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Get group chat started");
            final GroupChat response =
                    groupChatApplicationService.getGroupChat(chatId);
            controllerLogger.info("Get group chat finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get group chat failed", e);
            throw e;
        }
    }


    @PatchMapping(
            value = "/api/chat/group/{chatId}/metadata",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> changeGroupChatMetadata(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UpdateGroupChatMetadataRequest request
    ) {
        try {
            controllerLogger.info("Change group chat metadata started");
            final HttpResponse response =
                    groupChatApplicationService.changeGroupChatMetadata(chatId, request);
            controllerLogger.info("Change group chat metadata finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Change group chat metadata failed", e);
            throw e;
        }
    }


    @PatchMapping(
            value = "/api/chat/group/{chatId}/status",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> changeGroupChatPublicStatus(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Change group chat public status started");
            final HttpResponse response =
                    groupChatApplicationService.changeGroupChatPublicStatus(chatId);
            controllerLogger.info("Change group chat public status finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Change group chat public status failed", e);
            throw e;
        }
    }


    @PatchMapping(
            value = "/api/chat/group/{chatId}/owner",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> changeGroupChatOwner(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UserIdRequest request
    ) {
        try {
            controllerLogger.info("Change group chat owner started");
            final HttpResponse response =
                    groupChatApplicationService.changeGroupChatOwner(chatId, request);
            controllerLogger.info("Change group chat owner finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Change group chat owner failed", e);
            throw e;
        }
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Delete group chat started");
            final HttpResponse response =
                    groupChatApplicationService.deleteGroupChat(chatId);
            controllerLogger.info("Delete group chat finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Delete group chat failed", e);
            throw e;
        }
    }

}