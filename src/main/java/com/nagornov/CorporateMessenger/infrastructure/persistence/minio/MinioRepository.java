package com.nagornov.CorporateMessenger.infrastructure.persistence.minio;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;

    public void upload(String bucketName, String objectName, InputStream inputStream, long objectSize,  String contentType) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, objectSize, -1)
                        .contentType(contentType)
                        .build()
        );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to Minio" + e.getMessage());
        }
    }

    public InputStream download(String bucketName, String objectName) {
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

    public void delete(String bucketName, String objectName) {
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

    public boolean objectExists(String bucketName, String objectName) {
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

    public StatObjectResponse statObject(String bucketName, String objectName) {
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