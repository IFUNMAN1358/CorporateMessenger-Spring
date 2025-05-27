package com.nagornov.CorporateMessenger.infrastructure.persistence.minio;

import com.nagornov.CorporateMessenger.domain.enums.minio.MinioBucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private final S3Client s3Client;

    public void upload(MinioBucket bucket, String objectPath, InputStream inputStream, String mimeType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket.getBucketName())
                    .key(objectPath)
                    .contentType(mimeType)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, inputStream.available()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to upload file to Minio [bucket=%s, path=%s, mimeType=%s]: %s"
                            .formatted(bucket.getBucketName(), objectPath, mimeType, e.getMessage()), e);
        }
    }

    public InputStream download(MinioBucket bucket, String objectPath) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket.getBucketName())
                    .key(objectPath)
                    .build();
            return s3Client.getObject(request);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to download file from Minio [bucket=%s]: %s"
                            .formatted(bucket.getBucketName(), e.getMessage())
            );
        }
    }

    public void delete(MinioBucket bucket, String objectPath) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket.getBucketName())
                    .key(objectPath)
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to delete file from Minio [bucket=%s]: %s"
                            .formatted(bucket.getBucketName(), e.getMessage())
            );
        }
    }

    public HeadObjectResponse statObject(MinioBucket bucket, String objectPath) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucket.getBucketName())
                    .key(objectPath)
                    .build();
            return s3Client.headObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object metadata from S3: " + e.getMessage(), e);
        }
    }

}