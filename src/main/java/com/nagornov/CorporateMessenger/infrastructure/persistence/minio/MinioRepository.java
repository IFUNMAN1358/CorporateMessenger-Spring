package com.nagornov.CorporateMessenger.infrastructure.persistence.minio;

import io.minio.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;

    public void upload(@NotNull String bucketName, @NotNull String objectName, @NotNull InputStream inputStream, @NotNull String contentType) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build()
        );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to Minio" + e.getMessage());
        }
    }

    public InputStream download(@NotNull String bucketName, @NotNull String objectName) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from Minio: " + e.getMessage());
        }
    }

    public void delete(@NotNull String bucketName, @NotNull String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Minio: " + e.getMessage());
        }
    }

    public boolean objectExists(@NotNull String bucketName, @NotNull String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public StatObjectResponse statObject(@NotNull String bucketName, @NotNull String objectName) {
        try {
            return minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get metadata file from Minio: " + e.getMessage());
        }
    }

}