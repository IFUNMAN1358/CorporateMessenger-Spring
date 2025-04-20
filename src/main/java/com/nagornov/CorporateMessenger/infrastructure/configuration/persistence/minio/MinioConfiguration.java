package com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.minio;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(
                        minioProperties.getHost(),
                        minioProperties.getPort(),
                        minioProperties.getSecure()
                )
                .credentials(
                        minioProperties.getAccessKey(),
                        minioProperties.getSecretKey()
                )
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(minioProperties.getUri()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                ))
                .region(Region.US_EAST_1)
                .build();
    }

}