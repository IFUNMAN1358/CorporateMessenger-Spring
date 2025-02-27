package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.SecondUserIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.PrivateChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.PrivateChatApplicationService;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrivateChatController {

    private final PrivateChatApplicationService privateChatApplicationService;
    private final ControllerLogger controllerLogger;

    @PostMapping(
            value = "/api/chat/private",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<PrivateChatSummaryResponse> getOrCreatePrivateChat(@Validated @RequestBody SecondUserIdRequest request) {
        try {
            controllerLogger.info("Get or create private chat started");
            final PrivateChatSummaryResponse response =
                    privateChatApplicationService.getOrCreatePrivateChat(request);
            controllerLogger.info("Get or create private chat finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get or create private chat failed", e);
            throw e;
        }
    }

    @GetMapping(
            value = "/api/chat/private/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<PrivateChatSummaryResponse>> getAllPrivateChats() {
        try {
            controllerLogger.info("Get all private chats started");
            final List<PrivateChatSummaryResponse> response =
                    privateChatApplicationService.getAllPrivateChats();
            controllerLogger.info("Get all private chats finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get all private chats failed", e);
            throw e;
        }
    }

    @GetMapping(
            value = "/api/chat/private/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<PrivateChatSummaryResponse> getPrivateChat(@PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Get private chat started");
            final PrivateChatSummaryResponse response =
                    privateChatApplicationService.getPrivateChat(chatId);
            controllerLogger.info("Get private chat finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get private chat failed", e);
            throw e;
        }
    }

    @PostMapping(
            value = "/api/chat/private/unavailable",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> markPrivateChatAsUnavailable(@Validated @RequestBody ChatIdRequest request) {
        try {
            controllerLogger.info("Mark private chat as unavailable started");
            final HttpResponse response =
                    privateChatApplicationService.markPrivateChatAsUnavailable(request);
            controllerLogger.info("Mark private chat as unavailable finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Mark private chat as unavailable failed", e);
            throw e;
        }
    }
}