package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatPhotoApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class GroupChatPhotoController {

    private final GroupChatPhotoApplicationService groupChatPhotoApplicationService;

    @GetMapping(
            value = "/api/chat/group/{chatId}/photo",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getGroupChatPhoto(@ValidUuid @PathVariable("chatId") String chatId) {
        Resource response =
                groupChatPhotoApplicationService.getGroupChatPhoto(chatId);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/api/chat/group/{chatId}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Resource> uploadOrChangeGroupChatPhoto(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @ModelAttribute FileRequest request
    ) {
        Resource response =
                groupChatPhotoApplicationService.uploadOrChangeGroupChatPhoto(chatId, request);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping(
            value = "/api/chat/group/{chatId}/photo",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteGroupChatPhoto(@ValidUuid @PathVariable("chatId") String chatId) {
        HttpResponse response =
                groupChatPhotoApplicationService.deleteGroupChatPhoto(chatId);
        return ResponseEntity.status(200).body(response);
    }

}
