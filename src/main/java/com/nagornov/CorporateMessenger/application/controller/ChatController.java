package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.model.chat.*;
import com.nagornov.CorporateMessenger.application.applicationService.ChatApplicationService;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatApplicationService chatApplicationService;

    @PostMapping(
            path = "/api/user/{userId}/chat/private",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatWithChatPhotoResponse> getOrCreatePrivateChatByUserId(
            @PathVariable UUID userId
    ) {
        ChatWithChatPhotoResponse response = chatApplicationService.getOrCreatePrivateChatByUserId(userId);
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            path = "/api/chat/group",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatWithChatPhotoResponse> createGroupChat(
            @Valid @RequestBody CreateGroupChatRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("CreateGroupChatRequest validation error", bindingResult);
        }
        ChatWithChatPhotoResponse response = chatApplicationService.createGroupChat(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/chat/group/exists-username",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Boolean> existsGroupChatByUsername(
            @RequestParam String username
    ) {
        Boolean response = chatApplicationService.existsGroupChatByUsername(username);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chat/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatWithChatPhotoResponse> getChat(
            @PathVariable Long chatId
    ) {
        ChatWithChatPhotoResponse response = chatApplicationService.getChat(chatId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chats",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<ChatWithChatPhotoResponse>> getAllChats() {
        List<ChatWithChatPhotoResponse> response = chatApplicationService.getAllChats();
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/chat/{chatId}/group/title",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeGroupChatTitle(
            @PathVariable Long chatId,
            @RequestBody GroupChatTitleRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("GroupChatTitleRequest validation error", bindingResult);
        }
        chatApplicationService.changeGroupChatTitle(chatId, request);
        return ResponseEntity.status(200).body("Group chat title changed");
    }


    @PatchMapping(
            path = "/api/chat/{chatId}/group/username",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeGroupChatUsername(
            @PathVariable Long chatId,
            @Valid @RequestBody GroupChatUsernameRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("GroupChatUsernameRequest validation error", bindingResult);
        }
        chatApplicationService.changeGroupChatUsername(chatId, request);
        return ResponseEntity.status(200).body("Group chat username changed");
    }


    @PatchMapping(
            path = "/api/chat/{chatId}/group/description",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeGroupChatDescription(
            @PathVariable Long chatId,
            @RequestBody GroupChatDescriptionRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("GroupChatDescriptionRequest validation error", bindingResult);
        }
        chatApplicationService.changeGroupChatDescription(chatId, request);
        return ResponseEntity.status(200).body("Group chat description changed");
    }


    @PatchMapping(
            path = "/api/chat/{chatId}/group/settings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> changeGroupChatSettings(
            @PathVariable Long chatId,
            @RequestBody GroupChatSettingsRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("GroupChatSettingsRequest validation error", bindingResult);
        }
        chatApplicationService.changeGroupChatSettings(chatId, request);
        return ResponseEntity.status(200).body("Group chat settings changed");
    }


    @DeleteMapping(
            path = "/api/chat/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteChatByChatId(
            @PathVariable Long chatId
    ) {
        chatApplicationService.deleteChatByChatId(chatId);
        return ResponseEntity.status(200).body("Chat deleted");
    }
}