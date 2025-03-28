package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.SecondUserIdRequest;
import com.nagornov.CorporateMessenger.application.dto.chat.PrivateChatSummaryResponse;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.PrivateChatApplicationService;
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

    @PostMapping(
            value = "/api/chat/private",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<PrivateChatSummaryResponse> getOrCreatePrivateChat(@Validated @RequestBody SecondUserIdRequest request) {
        PrivateChatSummaryResponse response =
                privateChatApplicationService.getOrCreatePrivateChat(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/chat/private/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<PrivateChatSummaryResponse>> getAllPrivateChats() {
        List<PrivateChatSummaryResponse> response =
                privateChatApplicationService.getAllPrivateChats();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/chat/private/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<PrivateChatSummaryResponse> getPrivateChat(@PathVariable("chatId") String chatId) {
        PrivateChatSummaryResponse response =
                privateChatApplicationService.getPrivateChat(chatId);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/api/chat/private/unavailable",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> markPrivateChatAsUnavailable(@Validated @RequestBody ChatIdRequest request) {
        HttpResponse response =
                privateChatApplicationService.markPrivateChatAsUnavailable(request);
        return ResponseEntity.status(200).body(response);
    }
}