package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.applicationService.MessageApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.message.*;
import com.nagornov.CorporateMessenger.domain.enums.WsMessageResponseType;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageApplicationService messageApplicationService;
    private final SimpMessagingTemplate messagingTemplate;


    @PostMapping(
            path = "/api/chat/{chatId}/message",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> createMessage(
            @PathVariable Long chatId,
            @ModelAttribute MessageRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) | MessageRequest validation error", bindingResult);
        }
        MessageResponse response = messageApplicationService.createMessage(chatId, request);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(chatId),
                new WsMessageResponse(WsMessageResponseType.CREATE, response)
        );
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/chat/{chatId}/messages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<MessageResponse>> getAllMessages(
            @PathVariable Long chatId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        List<MessageResponse> response = messageApplicationService.getAllMessages(chatId, page, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chat/{chatId}/message/{messageId}/file/{fileId}"
    )
    ResponseEntity<Resource> downloadMessageFile(
            @PathVariable Long chatId,
            @PathVariable UUID messageId,
            @PathVariable UUID fileId,
            @RequestParam String size // big | small
    ) {
        Resource response = messageApplicationService.downloadMessageFile(chatId, messageId, fileId, size);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/chat/{chatId}/message/{messageId}/read",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> readMessage(
            @PathVariable Long chatId,
            @PathVariable UUID messageId
    ) {
        MessageResponse response = messageApplicationService.readMessage(chatId, messageId);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(chatId),
                new WsMessageResponse(WsMessageResponseType.READ, response)
        );
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/chat/{chatId}/message/{messageId}/content",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> updateMessageContent(
            @PathVariable Long chatId,
            @PathVariable UUID messageId,
            @RequestBody MessageContentRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) | PathVariable(messageId) | MessageContentRequest validation error", bindingResult);
        }
        MessageResponse response = messageApplicationService.updateMessageContent(chatId, messageId, request);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(chatId),
                new WsMessageResponse(WsMessageResponseType.UPDATE_CONTENT, response)
        );
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            path = "/api/chat/{chatId}/message/{messageId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> deleteMessage(
            @PathVariable Long chatId,
            @PathVariable UUID messageId
    ) {
        MessageResponse response = messageApplicationService.deleteMessage(chatId, messageId);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(chatId),
                new WsMessageResponse(WsMessageResponseType.DELETE, response)
        );
        return ResponseEntity.status(200).body(response);
    }
}