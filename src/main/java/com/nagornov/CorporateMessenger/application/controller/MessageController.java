package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.MinioFileDto;
import com.nagornov.CorporateMessenger.application.applicationService.MessageApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.message.*;
import com.nagornov.CorporateMessenger.domain.enums.WsMessageResponseType;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
            value = "/api/message",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> createMessage(@ModelAttribute CreateMessageRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("CreateMessageRequest validation error", bindingResult);
        }
        MessageResponse response = messageApplicationService.createMessage(request);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(request.getChatId()),
                new WsMessageResponse(WsMessageResponseType.CREATE, response)
        );
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            value = "/api/message/{chatId}/{page}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<MessageResponse>> getAllMessages(
            @NotNull @PathVariable Long chatId,
            @PathVariable int page,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) or PathVariable(page) validation error", bindingResult);
        }
        List<MessageResponse> response = messageApplicationService.getAllMessages(chatId, page, 20);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
        value = "/api/message/content",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> updateMessageContent(@RequestBody UpdateMessageContentRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("UpdateMessageContentRequest validation error", bindingResult);
        }
        MessageResponse response = messageApplicationService.updateMessageContent(request);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(request.getChatId()),
                new WsMessageResponse(WsMessageResponseType.UPDATE_CONTENT, response)
        );
        return ResponseEntity.status(200).body(response);
    }


    @DeleteMapping(
            value = "/api/message",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> deleteMessage(@RequestBody DeleteMessageRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("DeleteMessageRequest validation error", bindingResult);
        }
        MessageResponse response = messageApplicationService.deleteMessage(request);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(request.getChatId()),
                new WsMessageResponse(WsMessageResponseType.DELETE, response)
        );
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            value = "/api/message/read",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> readMessage(@RequestBody ReadMessageRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("ReadMessageRequest validation error", bindingResult);
        }
        MessageResponse response = messageApplicationService.readMessage(request);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(request.getChatId()),
                new WsMessageResponse(WsMessageResponseType.READ, response)
        );
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            value = "/api/chat/{chatId}/message/{messageId}/file/{fileId}"
    )
    ResponseEntity<Resource> getMessageFile(
            @NotNull @PathVariable Long chatId,
            @NotNull @PathVariable UUID messageId,
            @NotNull @PathVariable UUID fileId,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) or PathVariable(messageId) or PathVariable(fileId) validation error", bindingResult);
        }
        MinioFileDto minioFileDto = messageApplicationService.getMessageFile(chatId, messageId, fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(minioFileDto.getStatObject().contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + minioFileDto.getFile().getFilename() + "\""
                )
                .body(minioFileDto.getFile());
    }
}