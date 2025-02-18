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
    ResponseEntity<PrivateChatSummaryResponse> getOrCreate(@Validated @RequestBody SecondUserIdRequest request) {
        final PrivateChatSummaryResponse response =
                privateChatApplicationService.getOrCreate(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/chat/private/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<PrivateChatSummaryResponse>> getAll() {
        final List<PrivateChatSummaryResponse> response =
                privateChatApplicationService.getAll();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/api/chat/private/{chatId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<PrivateChatSummaryResponse> get(@PathVariable("chatId") String chatId) {
        final PrivateChatSummaryResponse response =
                privateChatApplicationService.get(chatId);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/api/chat/private/unavailable",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> markAsUnavailable(@Validated @RequestBody ChatIdRequest request) {
        final HttpResponse response =
                privateChatApplicationService.markAsUnavailable(request);
        return ResponseEntity.status(200).body(response);
    }
}