package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.UpdateGroupChatMetadataRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.GroupChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.InformationalResponse;
import com.nagornov.CorporateMessenger.application.dto.common.UserIdRequest;
import com.nagornov.CorporateMessenger.application.service.GroupChatApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
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

    @PostMapping(
            value = "/api/chat/group",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> createGroupChat(@Validated @ModelAttribute CreateGroupChatRequest request) {
        final InformationalResponse response =
                groupChatApplicationService.createGroupChat(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/chat/group/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<GroupChatSummaryResponse>> getAllGroupChats() {
        final List<GroupChatSummaryResponse> response =
                groupChatApplicationService.getAllGroupChats();
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/chat/group/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<GroupChat> getGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        final GroupChat response =
                groupChatApplicationService.getGroupChat(chatId);
        return ResponseEntity.status(201).body(response);
    }


    @PatchMapping(
            value = "/api/chat/group/{chatId}/metadata",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> changeGroupChatMetadata(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UpdateGroupChatMetadataRequest request
    ) {
        final InformationalResponse response =
                groupChatApplicationService.changeGroupChatMetadata(chatId, request);
        return ResponseEntity.status(201).body(response);
    }


    @PatchMapping(
            value = "/api/chat/group/{chatId}/status",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> changeGroupChatPublicStatus(@ValidUuid @PathVariable("chatId") String chatId) {
        final InformationalResponse response =
                groupChatApplicationService.changeGroupChatPublicStatus(chatId);
        return ResponseEntity.status(201).body(response);
    }


    @PatchMapping(
            value = "/api/chat/group/{chatId}/owner",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> changeGroupChatOwner(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @RequestBody UserIdRequest request
    ) {
        InformationalResponse infoResponse =
                groupChatApplicationService.changeGroupChatOwner(chatId, request);
        return ResponseEntity.status(200).body(infoResponse);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> deleteGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        final InformationalResponse response =
                groupChatApplicationService.deleteGroupChat(chatId);
        return ResponseEntity.status(201).body(response);
    }

}