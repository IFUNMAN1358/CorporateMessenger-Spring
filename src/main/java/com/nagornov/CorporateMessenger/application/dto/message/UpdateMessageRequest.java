package com.nagornov.CorporateMessenger.application.dto.message;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateMessageRequest {

    @NotBlank
    @NotNull
    @ValidUuid
    private String chatId;

    @NotBlank
    @NotNull
    @ValidUuid
    private String messageId;

    private String content;

    private List<MultipartFile> files;

    @AssertTrue
    public boolean isValidFileCount() {
        if (this.files != null) {
            return this.files.size() <= 10;
        }
        return true;
    }

    @AssertTrue
    public boolean isContentAndFiles() {
        boolean hasContent = this.content != null && !this.content.isBlank();
        boolean hasFiles = this.files != null && !this.files.isEmpty();
        return hasContent || hasFiles;
    }

    public UUID getChatIdAsUUID() {
        return UUID.fromString(this.chatId);
    }

    public UUID getMessageIdAsUUID() {
        return UUID.fromString(this.messageId);
    }

}