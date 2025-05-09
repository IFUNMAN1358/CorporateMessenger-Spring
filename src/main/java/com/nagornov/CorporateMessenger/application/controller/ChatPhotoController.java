package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.applicationService.ChatPhotoApplicationService;
import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatPhotoResponse;
import com.nagornov.CorporateMessenger.domain.exception.BindingErrorException;
import jakarta.validation.constraints.NotNull;
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
            @NotNull @PathVariable Long chatId,
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
    ResponseEntity<List<ChatPhotoResponse>> getAllGroupChatPhotos(@NotNull @PathVariable Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        List<ChatPhotoResponse> response = chatPhotoApplicationService.getAllGroupChatPhotos(chatId);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chat/group/{chatId}/photo/main",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadMainGroupChatPhoto(
            @NotNull @PathVariable Long chatId,
            @NotNull @RequestParam String size, // big | small
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) | RequestParam(size) validation error", bindingResult);
        }
        Resource response = chatPhotoApplicationService.downloadMainGroupChatPhoto(chatId, size);
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(
            path = "/api/chat/group/{chatId}/photo/{photoId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> downloadGroupChatPhotoByPhotoId(
            @NotNull @PathVariable Long chatId,
            @NotNull @PathVariable UUID photoId,
            @NotNull @RequestParam String size, // big | small
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) | PathVariable(photoId) | RequestParam(size) validation error", bindingResult);
        }
        Resource response = chatPhotoApplicationService.downloadGroupChatPhoto(chatId, photoId, size);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping(
            path = "/api/chat/group/{chatId}/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<ChatPhotoResponse> setMainGroupChatPhoto(
            @NotNull @PathVariable Long chatId,
            @NotNull @PathVariable UUID photoId,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) | PathVariable(photoId) validation error", bindingResult);
        }
        ChatPhotoResponse response = chatPhotoApplicationService.setMainGroupChatPhoto(chatId, photoId);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(
            path = "/api/chat/group/{chatId}/photo/{photoId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> deleteChatPhoto(
            @NotNull @PathVariable Long chatId,
            @NotNull @PathVariable UUID photoId,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) | PathVariable(photoId) validation error", bindingResult);
        }
        chatPhotoApplicationService.deleteGroupChatPhoto(chatId, photoId);
        return ResponseEntity.status(204).body("Photo of group chat deleted");
    }
}