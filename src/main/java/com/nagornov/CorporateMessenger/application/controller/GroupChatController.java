package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.UpdateGroupChatMetadataRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.GroupChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.user.UserIdRequest;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
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
    ResponseEntity<HttpResponse> createGroupChat(@Validated @ModelAttribute CreateGroupChatRequest request) {
        HttpResponse response =
                groupChatApplicationService.createGroupChat(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/chat/group/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<GroupChatSummaryResponse>> getAllGroupChats() {
        List<GroupChatSummaryResponse> response =
                groupChatApplicationService.getAllGroupChats();
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/chat/group/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<GroupChat> getGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        GroupChat response =
                groupChatApplicationService.getGroupChat(chatId);
        return ResponseEntity.status(200).body(response);
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
        HttpResponse response =
                groupChatApplicationService.changeGroupChatMetadata(chatId, request);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            value = "/api/chat/group/{chatId}/status",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> changeGroupChatPublicStatus(@ValidUuid @PathVariable("chatId") String chatId) {
        HttpResponse response =
                groupChatApplicationService.changeGroupChatPublicStatus(chatId);
        return ResponseEntity.status(200).body(response);
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
        HttpResponse response =
                groupChatApplicationService.changeGroupChatOwner(chatId, request);
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteGroupChat(@ValidUuid @PathVariable("chatId") String chatId) {
        HttpResponse response =
                groupChatApplicationService.deleteGroupChat(chatId);
        return ResponseEntity.status(200).body(response);
    }

}