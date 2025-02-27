package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.applicationService.GroupChatPhotoApplicationService;
import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
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
    private final ControllerLogger controllerLogger;

    @GetMapping(
            value = "/api/chat/group/{chatId}/photo",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    ResponseEntity<Resource> getGroupChatPhoto(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Get group chat photo started");
            final Resource response =
                    groupChatPhotoApplicationService.getGroupChatPhoto(chatId);
            controllerLogger.info("Get group chat photo finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Get group chat photo failed", e);
            throw e;
        }
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
        try {
            controllerLogger.info("Upload or change group chat photo started");
            final Resource response =
                    groupChatPhotoApplicationService.uploadOrChangeGroupChatPhoto(chatId, request);
            controllerLogger.info("Upload or change group chat photo finished");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            controllerLogger.error("Upload or change group chat photo failed", e);
            throw e;
        }
    }

    @DeleteMapping(
            value = "/api/chat/group/{chatId}/photo",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HttpResponse> deleteGroupChatPhoto(@ValidUuid @PathVariable("chatId") String chatId) {
        try {
            controllerLogger.info("Delete group chat photo started");
            final HttpResponse response =
                    groupChatPhotoApplicationService.deleteGroupChatPhoto(chatId);
            controllerLogger.info("Delete group chat photo finished");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            controllerLogger.error("Delete group chat photo failed", e);
            throw e;
        }
    }

}
