package com.nagornov.CorporateMessenger.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

@Data
@AllArgsConstructor
public class MinioFileDTO {

    private Resource resource;
    private HeadObjectResponse headObjectResponse;

}
