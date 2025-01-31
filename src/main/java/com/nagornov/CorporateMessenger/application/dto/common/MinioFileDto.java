package com.nagornov.CorporateMessenger.application.dto.common;

import io.minio.StatObjectResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
public class MinioFileDto {

    @NotBlank
    @NotNull
    private Resource file;

    @NotBlank
    @NotNull
    private StatObjectResponse statObject;

}
