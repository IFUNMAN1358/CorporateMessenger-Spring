package com.nagornov.CorporateMessenger.application.dto.model.message;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MessageRequest {

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

}