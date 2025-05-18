package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.applicationService.ChatPhotoApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatPhotoResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@Controller
@RequiredArgsConstructor
public class ChatPhotoController {

    private final ChatPhotoApplicationService chatPhotoApplicationService;

    @PostMapping(
            path = "/api/chat/group/{chatId}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatPhotoResponse> uploadGroupChatPhoto(
            @PathVariable Long chatId,
            @ModelAttribute FileRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) | ModelAttribute(FileRequest) validation error", bindingResult);
        }
        ChatPhotoResponse response = chatPhotoApplicationService.uploadGroupChatPhoto(chatId, request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping(
            path = "/api/chat/group/{chatId}/photos",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<ChatPhotoResponse>> getAllGroupChatPhotosByChatId(
            @PathVariable Long chatId
    ) {
        List<ChatPhotoResponse> response = chatPhotoApplicationService.getAllGroupChatPhotosByChatId(chatId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chat/group/{chatId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMainGroupChatPhotoByChatId(
            @PathVariable Long chatId,
            @RequestParam String size // big | small
    ) {
        Resource response = chatPhotoApplicationService.downloadMainGroupChatPhotoByChatId(chatId, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chat/group/{chatId}/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadGroupChatPhotoByChatIdAndPhotoId(
            @PathVariable Long chatId,
            @PathVariable UUID photoId,
            @RequestParam String size // big | small
    ) {
        Resource response = chatPhotoApplicationService.downloadGroupChatPhotoByChatIdAndPhotoId(chatId, photoId, size);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/chat/group/{chatId}/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatPhotoResponse> setMainGroupChatPhotoByChatIdAndPhotoId(
            @PathVariable Long chatId,
            @PathVariable UUID photoId
    ) {
        ChatPhotoResponse response = chatPhotoApplicationService.setMainGroupChatPhotoByChatIdAndPhotoId(chatId, photoId);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(
            path = "/api/chat/group/{chatId}/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteChatPhotoByChatIdAndPhotoId(
            @PathVariable Long chatId,
            @PathVariable UUID photoId
    ) {
        chatPhotoApplicationService.deleteGroupChatPhotoByChatIdAndPhotoId(chatId, photoId);
        return ResponseEntity.status(204).body("Photo of group chat deleted");
    }
}