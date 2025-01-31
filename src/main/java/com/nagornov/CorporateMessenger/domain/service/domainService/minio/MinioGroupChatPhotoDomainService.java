package com.nagornov.CorporateMessenger.domain.service.domainService.minio;

import com.nagornov.CorporateMessenger.domain.enums.ContentType;
import com.nagornov.CorporateMessenger.domain.enums.MinioBucket;
import com.nagornov.CorporateMessenger.domain.logger.MinioLogger;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import io.minio.StatObjectResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioGroupChatPhotoDomainService {

    private final MinioRepository minioRepository;
    private final MinioLogger minioLogger;

    public String upload(@NotNull MultipartFile file) {
        try {
            final String filePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioRepository.upload(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath,
                file.getInputStream(),
                ContentType.IMAGE_JPEG.getContentType()
            );
            return filePath;
        } catch (Exception e) {
            minioLogger.error("Error uploading group chat photo in service " + e.getMessage());
            throw new RuntimeException("Error uploading group chat photo in service " + e.getMessage());
        }
    }

    public InputStream download(@NotNull String filePath) {
        try {
            return minioRepository.download(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            minioLogger.error("Error downloading group chat photo in service " + e.getMessage());
            throw new RuntimeException("Error downloading group chat photo in service " + e.getMessage());
        }
    }

    public void delete(@NotNull String filePath) {
        try {
            minioRepository.delete(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            minioLogger.error("Error deleting group chat photo in service " + e.getMessage());
            throw new RuntimeException("Error deleting group chat photo in service " + e.getMessage());
        }
    }


    public StatObjectResponse statObject(@NotNull String filePath) {
        try {
            return minioRepository.statObject(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            minioLogger.error("Error get metadata about group chat photo in service " + e.getMessage());
            throw new RuntimeException("Error get metadata about group chat photo in service " + e.getMessage());
        }
    }

}
