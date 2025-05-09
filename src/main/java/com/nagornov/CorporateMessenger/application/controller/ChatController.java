package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatWithChatPhotoResponse;
import com.nagornov.CorporateMessenger.application.applicationService.ChatApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.model.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserIdRequest;
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
public class ChatController {

    private final ChatApplicationService chatApplicationService;

    @PostMapping(
            path = "/api/chat/private",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatWithChatPhotoResponse> getOrCreatePrivateChat(@RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest validation error", bindingResult);
        }
        ChatWithChatPhotoResponse response = chatApplicationService.getOrCreatePrivateChat(request);
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            path = "/api/chat/group",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatWithChatPhotoResponse> createGroupChat(@RequestBody CreateGroupChatRequest request, BindingResult bindingResult) {
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
    ResponseEntity<ChatWithChatPhotoResponse> getChat(@NotNull @PathVariable Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(userId) validation error", bindingResult);
        }
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
            path = "/api/chat",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteChat(@RequestBody ChatIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("ChatIdRequest validation error", bindingResult);
        }
        chatApplicationService.deleteChat(request);
        return ResponseEntity.status(200).body("Chat deleted");
    }
}