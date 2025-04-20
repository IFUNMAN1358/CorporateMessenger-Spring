package com.nagornov.CorporateMessenger.infrastructure.persistence.minio;

import com.nagornov.CorporateMessenger.domain.enums.MinioBucket;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;
    private final S3Client s3Client;

    public void upload(MinioBucket bucket, String objectPath, BufferedImage image, String mimeType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, mimeType, baos);
        byte[] imageBytes = baos.toByteArray();

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket.getBucketName())
                .key(objectPath)
                .contentType(mimeType)
                .build(),
            RequestBody.fromInputStream(new ByteArrayInputStream(imageBytes), imageBytes.length)
        );
    }

    public void upload(MinioBucket bucket, String objectPath, InputStream inputStream, long objectSize, String mimeType) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket.getBucketName())
                        .object(objectPath)
                        .stream(inputStream, objectSize, -1)
                        .contentType(mimeType)
                        .build()
        );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to Minio" + e.getMessage());
        }
    }

    public InputStream download(MinioBucket bucket, String objectPath) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket.getBucketName())
                    .object(objectPath)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from Minio: " + e.getMessage());
        }
    }

    public void delete(MinioBucket bucket, String objectPath) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket.getBucketName())
                    .object(objectPath)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Minio: " + e.getMessage());
        }
    }

    public boolean objectExists(MinioBucket bucket, String objectPath) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket.getBucketName())
                    .object(objectPath)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public StatObjectResponse statObject(MinioBucket bucket, String objectPath) {
        try {
            return minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket.getBucketName())
                    .object(objectPath)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get metadata file from Minio: " + e.getMessage());
        }
    }

}