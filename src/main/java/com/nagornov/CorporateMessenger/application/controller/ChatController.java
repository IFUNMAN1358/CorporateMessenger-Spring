package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatDTO;
import com.nagornov.CorporateMessenger.application.applicationService.ChatApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.model.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
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
            value = "/api/chat/private",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatDTO> getOrCreatePrivateChat(@RequestBody UserIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UserIdRequest validation error", bindingResult);
        }
        ChatDTO response = chatApplicationService.getOrCreatePrivateChat(request);
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            value = "/api/chat/group",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatDTO> createGroupChat(@RequestBody CreateGroupChatRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("CreateGroupChatRequest validation error", bindingResult);
        }
        ChatDTO response = chatApplicationService.createGroupChat(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/chat/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatDTO> getChat(@NotNull @PathVariable Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(userId) validation error", bindingResult);
        }
        ChatDTO response = chatApplicationService.getChat(chatId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/chats",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<ChatDTO>> getAllChats() {
        List<ChatDTO> response = chatApplicationService.getAllChats();
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            value = "/api/chat",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteChat(@RequestBody ChatIdRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("ChatIdRequest validation error", bindingResult);
        }
        HttpResponse response = chatApplicationService.deleteChat(request);
        return ResponseEntity.status(200).body(response);
    }
}