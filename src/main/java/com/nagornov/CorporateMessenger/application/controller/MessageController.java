package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.MinioFileDto;
import com.nagornov.CorporateMessenger.application.dto.message.*;
import com.nagornov.CorporateMessenger.application.service.MessageApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.enums.WsMessageResponseType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ResponseEntity<MessageResponse> create(@Validated @ModelAttribute CreateMessageRequest request) {
        final MessageResponse response =
                messageApplicationService.create(request);
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
    ResponseEntity<List<MessageResponse>> getAll(@PathVariable("chatId") @ValidUuid String chatId, @PathVariable("page") int page) {
        final List<MessageResponse> response =
                messageApplicationService.getAll(chatId, page, 20);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
        value = "/api/message/content",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> updateContent(@Validated @RequestBody UpdateMessageContentRequest request) {
        final MessageResponse response =
                messageApplicationService.updateContent(request);
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
    ResponseEntity<MessageResponse> delete(@Validated @RequestBody DeleteMessageRequest request) {
        final MessageResponse response =
                messageApplicationService.delete(request);
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
    ResponseEntity<MessageResponse> read(@Validated @RequestBody ReadMessageRequest request) {
        final MessageResponse response =
                messageApplicationService.read(request);
        messagingTemplate.convertAndSend(
                "/topic/chat/%s".formatted(request.getChatId()),
                new WsMessageResponse(WsMessageResponseType.READ, response)
        );
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(value = "/api/chat/{chatId}/message/{messageId}/file/{fileId}")
    ResponseEntity<Resource> getFile(
            @PathVariable("chatId") @ValidUuid String chatId,
            @PathVariable("messageId") @ValidUuid String messageId,
            @PathVariable("fileId") @ValidUuid String fileId
    ) {
        final MinioFileDto minioFileDto =
                messageApplicationService.getFile(chatId, messageId, fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(minioFileDto.getStatObject().contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + minioFileDto.getFile().getFilename() + "\""
                )
                .body(minioFileDto.getFile());
    }
}