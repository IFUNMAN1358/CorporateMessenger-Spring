package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatWithChatPhotoResponse;
import com.nagornov.CorporateMessenger.application.applicationService.ChatApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
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
            consumes = MediaType.APPLICATION_JSON_VALUE,
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
            @RequestBody CreateGroupChatRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("CreateGroupChatRequest validation error", bindingResult);
        }
        ChatWithChatPhotoResponse response = chatApplicationService.createGroupChat(request);
        return ResponseEntity.status(201).body(response);
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


    @DeleteMapping(
            path = "/api/chat/{chatId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteChatByChatId(
            @PathVariable Long chatId
    ) {
        chatApplicationService.deleteChatByChatId(chatId);
        return ResponseEntity.status(200).body("Chat deleted");
    }
}