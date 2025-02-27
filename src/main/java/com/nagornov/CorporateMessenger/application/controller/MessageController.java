package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.MinioFileDto;
import com.nagornov.CorporateMessenger.application.dto.message.*;
import com.nagornov.CorporateMessenger.application.applicationService.MessageApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.enums.WsMessageResponseType;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
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
    private final ControllerLogger controllerLogger;

    @PostMapping(
            value = "/api/message",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> createMessage(@Validated @ModelAttribute CreateMessageRequest request) {
        try {
            controllerLogger.info("Create message started");

            final MessageResponse response =
                    messageApplicationService.createMessage(request);

            messagingTemplate.convertAndSend(
                    "/topic/chat/%s".formatted(request.getChatId()),
                    new WsMessageResponse(WsMessageResponseType.CREATE, response)
            );

            controllerLogger.info("Create message finished");
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            controllerLogger.error("Create message failed", e);
            throw e;
        }
    }


    @GetMapping(
            value = "/api/message/{chatId}/{page}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<MessageResponse>> getAllMessages(
            @PathVariable("chatId") @ValidUuid String chatId,
            @PathVariable("page") int page
    ) {
        try {
            controllerLogger.info("Get all messages started");

            final List<MessageResponse> response =
                    messageApplicationService.getAllMessages(chatId, page, 20);

            controllerLogger.info("Get all messages finished");
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            controllerLogger.error("Get all messages failed", e);
            throw e;
        }
    }


    @PatchMapping(
        value = "/api/message/content",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> updateMessageContent(@Validated @RequestBody UpdateMessageContentRequest request) {
        try {
            controllerLogger.info("Update message content started");

            final MessageResponse response =
                    messageApplicationService.updateMessageContent(request);

            messagingTemplate.convertAndSend(
                    "/topic/chat/%s".formatted(request.getChatId()),
                    new WsMessageResponse(WsMessageResponseType.UPDATE_CONTENT, response)
            );

            controllerLogger.info("Update message content finished");
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            controllerLogger.error("Update message content failed", e);
            throw e;
        }
    }


    @DeleteMapping(
            value = "/api/message",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> deleteMessage(@Validated @RequestBody DeleteMessageRequest request) {
        try {
            controllerLogger.info("Delete message started");

            final MessageResponse response =
                    messageApplicationService.deleteMessage(request);

            messagingTemplate.convertAndSend(
                    "/topic/chat/%s".formatted(request.getChatId()),
                    new WsMessageResponse(WsMessageResponseType.DELETE, response)
            );

            controllerLogger.info("Delete message finished");
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            controllerLogger.error("Delete message failed", e);
            throw e;
        }
    }


    @PostMapping(
            value = "/api/message/read",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<MessageResponse> readMessage(@Validated @RequestBody ReadMessageRequest request) {
        try {
            controllerLogger.info("Read message started");

            final MessageResponse response =
                    messageApplicationService.readMessage(request);

            messagingTemplate.convertAndSend(
                    "/topic/chat/%s".formatted(request.getChatId()),
                    new WsMessageResponse(WsMessageResponseType.READ, response)
            );

            controllerLogger.info("Read message finished");
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            controllerLogger.error("Read message failed", e);
            throw e;
        }
    }


    @GetMapping(value = "/api/chat/{chatId}/message/{messageId}/file/{fileId}")
    ResponseEntity<Resource> getMessageFile(
            @PathVariable("chatId") @ValidUuid String chatId,
            @PathVariable("messageId") @ValidUuid String messageId,
            @PathVariable("fileId") @ValidUuid String fileId
    ) {
        try {
            controllerLogger.info("Get message file started");

            final MinioFileDto minioFileDto =
                    messageApplicationService.getMessageFile(chatId, messageId, fileId);

            controllerLogger.info("Get message file finished");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(minioFileDto.getStatObject().contentType()))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + minioFileDto.getFile().getFilename() + "\""
                    )
                    .body(minioFileDto.getFile());

        } catch (Exception e) {
            controllerLogger.error("Get message file failed", e);
            throw e;
        }
    }
}