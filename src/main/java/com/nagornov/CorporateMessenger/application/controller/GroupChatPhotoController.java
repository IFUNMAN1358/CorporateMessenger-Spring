package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatPhotoApplicationService;
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

@Validated
@Controller
@RequiredArgsConstructor
public class GroupChatPhotoController {

    private final GroupChatPhotoApplicationService groupChatPhotoApplicationService;


    @GetMapping(
            value = "/api/chat/group/{chatId}/photo",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getGroupChatPhoto(@NotNull @PathVariable Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        Resource response = groupChatPhotoApplicationService.getGroupChatPhoto(chatId);
        return ResponseEntity.status(200).body(response);
    }


    @PostMapping(
            value = "/api/chat/group/{chatId}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Resource> uploadOrChangeGroupChatPhoto(
            @NotNull @PathVariable Long chatId,
            @ModelAttribute FileRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) or FileRequest validation error", bindingResult);
        }
        Resource response = groupChatPhotoApplicationService.uploadOrChangeGroupChatPhoto(chatId, request);
        return ResponseEntity.status(201).body(response);
    }


    @DeleteMapping(
            value = "/api/chat/group/{chatId}/photo",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteGroupChatPhoto(@NotNull @PathVariable Long chatId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BindingErrorException("PathVariable(chatId) validation error", bindingResult);
        }
        HttpResponse response = groupChatPhotoApplicationService.deleteGroupChatPhoto(chatId);
        return ResponseEntity.status(200).body(response);
    }
}