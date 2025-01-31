package com.nagornov.CorporateMessenger.domain.service.domainService.minio;

import com.nagornov.CorporateMessenger.domain.enums.ContentType;
import com.nagornov.CorporateMessenger.domain.enums.MinioBucket;
import com.nagornov.CorporateMessenger.domain.logger.MinioLogger;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioUserProfilePhotoDomainService {

    private final MinioRepository minioRepository;
    private final MinioLogger minioLogger;

    public String upload(@NotNull InputStream inputStream) {
        try {
            final String objectName = String.valueOf(UUID.randomUUID());
            minioRepository.upload(
                MinioBucket.USER_PROFILE_PHOTOS.getBucketName(),
                objectName,
                inputStream,
                ContentType.IMAGE_JPEG.getContentType()
            );
            return objectName;
        } catch (Exception e) {
            minioLogger.error("Error uploading user profile photo in service " + e.getMessage());
            throw new RuntimeException("Error uploading user profile photo in service " + e.getMessage());
        }
    }

    public InputStream download(@NotNull String objectName) {
        try {
            return minioRepository.download(
                MinioBucket.USER_PROFILE_PHOTOS.getBucketName(),
                objectName
            );
        } catch (Exception e) {
            minioLogger.error("Error downloading user profile photo in service " + e.getMessage());
            throw new RuntimeException("Error downloading user profile photo in service " + e.getMessage());
        }
    }

    public void delete(@NotNull String objectName) {
        try {
            minioRepository.delete(
                MinioBucket.USER_PROFILE_PHOTOS.getBucketName(),
                objectName
            );
        } catch (Exception e) {
            minioLogger.error("Error deleting user profile photo in service " + e.getMessage());
            throw new RuntimeException("Error deleting user profile photo in service " + e.getMessage());
        }
    }

}
