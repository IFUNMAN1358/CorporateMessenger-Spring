package com.nagornov.CorporateMessenger.domain.service.domainService.minio;

import com.nagornov.CorporateMessenger.domain.enums.ContentType;
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
public class MinioUserPhotoDomainService {

    private final MinioRepository minioRepository;

    public String upload(@NonNull MultipartFile file) {
        try {
            final String filePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioRepository.upload(
                MinioBucket.USER_PROFILE_PHOTOS.getBucketName(),
                filePath,
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
            );
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading user photo in service " + e.getMessage());
        }
    }

    public InputStream download(@NonNull String objectName) {
        try {
            return minioRepository.download(
                MinioBucket.USER_PROFILE_PHOTOS.getBucketName(),
                objectName
            );
        } catch (Exception e) {
            throw new RuntimeException("Error downloading user photo in service " + e.getMessage());
        }
    }

    public void delete(@NonNull String objectName) {
        try {
            minioRepository.delete(
                MinioBucket.USER_PROFILE_PHOTOS.getBucketName(),
                objectName
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user photo in service " + e.getMessage());
        }
    }

}
