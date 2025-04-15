package com.nagornov.CorporateMessenger.domain.service.domainService.minio;

import com.nagornov.CorporateMessenger.domain.enums.MinioBucket;
import com.nagornov.CorporateMessenger.infrastructure.persistence.minio.MinioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioGroupChatPhotoDomainService {

    private final MinioRepository minioRepository;

    public String upload(@NonNull MultipartFile file) {
        try {
            final String filePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioRepository.upload(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath,
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
            );
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading group chat photo in service " + e.getMessage());
        }
    }

    public InputStream download(@NonNull String filePath) {
        try {
            return minioRepository.download(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            throw new RuntimeException("Error downloading group chat photo in service " + e.getMessage());
        }
    }

    public void delete(@NonNull String filePath) {
        try {
            minioRepository.delete(
                MinioBucket.GROUP_CHAT_PHOTOS.getBucketName(),
                filePath
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting group chat photo in service " + e.getMessage());
        }
    }

}
