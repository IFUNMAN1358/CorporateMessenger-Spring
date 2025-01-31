package com.nagornov.CorporateMessenger.application.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileRequest {

    @NotNull
    private MultipartFile file;

}
