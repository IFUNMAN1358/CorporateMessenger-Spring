package com.nagornov.CorporateMessenger.infrastructure.persistence.minio;

import com.nagornov.CorporateMessenger.domain.enums.minio.MinioBucket;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;
    private final S3Client s3Client;

    public void upload(MinioBucket bucket, String objectPath, BufferedImage image, String mimeType) {
        // converting to mimeType
        try {
            ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
            ImageIO.write(image, mimeType, imageOutput);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket.getBucketName())
                    .key(objectPath)
                    .contentType(mimeType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(imageOutput.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to upload file to Minio [bucket=%s, mimeType=%s]: %s"
                            .formatted(bucket.getBucketName(), mimeType, e.getMessage())
            );
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