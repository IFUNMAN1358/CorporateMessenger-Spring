package com.nagornov.CorporateMessenger.domain.service.domainService.minio;

import com.nagornov.CorporateMessenger.domain.enums.MinioBucket;
import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import io.minio.StatObjectResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioMessageFileDomainService {

    private final MinioRepository minioRepository;

    public void upload(@NonNull MessageFile messageFile, @NonNull MultipartFile file) {
        try {
            minioRepository.upload(
                MinioBucket.MESSAGE_FILES.getBucketName(),
                messageFile.getFilePath(),
                file.getInputStream(),
                file.getContentType()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error uploading message file in service " + e.getMessage());
        }
    }

    public InputStream download(@NonNull String filePath) {
        try {
            return minioRepository.download(
                MinioBucket.MESSAGE_FILES.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            throw new RuntimeException("Error downloading message file in service " + e.getMessage());
        }
    }

    public void delete(@NonNull String filePath) {
        try {
            minioRepository.delete(
                MinioBucket.MESSAGE_FILES.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting message file in service " + e.getMessage());
        }
    }


    public StatObjectResponse statObject(@NonNull String filePath) {
        try {
            return minioRepository.statObject(
                    MinioBucket.MESSAGE_FILES.getBucketName(),
                    filePath
            );
        } catch (Exception e) {
            throw new RuntimeException("Error get metadata about message file in service " + e.getMessage());
        }
    }

}
