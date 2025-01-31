package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.InformationalResponse;
import com.nagornov.CorporateMessenger.application.service.GroupChatPhotoApplicationService;
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
    ResponseEntity<Resource> getPhoto(@ValidUuid @PathVariable("chatId") String chatId) {
        final Resource response =
                groupChatPhotoApplicationService.getPhoto(chatId);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/api/chat/group/{chatId}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Resource> uploadOrChangePhoto(
            @ValidUuid @PathVariable("chatId") String chatId,
            @Validated @ModelAttribute FileRequest request
    ) {
        final Resource response =
                groupChatPhotoApplicationService.uploadOrChangePhoto(chatId, request);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(
            value = "/api/chat/group/{chatId}/photo",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<InformationalResponse> deletePhoto(@ValidUuid @PathVariable("chatId") String chatId) {
        final InformationalResponse response =
                groupChatPhotoApplicationService.deletePhoto(chatId);
        return ResponseEntity.status(200).body(response);
    }

}
