package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Data
public class CreateGroupChatRequest {

    private MultipartFile file;

    @NotNull(message = "Название чата не может быть null")
    @NotBlank(message = "Название чата не может быть пустым")
    @Size(message = "Название чата должно содержать от 1 до 128 символов", min = 1, max = 128)
    private String title;

    //
    //
    //

    @PostConstruct
    public void initialize() {
        if (this.file == null) {
            throw new ResourceConflictException(
                    "CreateGroupChatRequest[file] can't be a null",
                    new FieldError("file", "Файл не может быть null")
            );
        }
        if (this.file.isEmpty()) {
            throw new ResourceConflictException(
                    "CreateGroupChatRequest[file] can't be a empty",
                    new FieldError("file", "Файл не может быть пустым")
            );
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new ResourceConflictException(
                    "CreateGroupChatRequest[file] from request is unsupported content type",
                    new FieldError("file", "Файл должен иметь изображением")
            );
        }
    }

}
