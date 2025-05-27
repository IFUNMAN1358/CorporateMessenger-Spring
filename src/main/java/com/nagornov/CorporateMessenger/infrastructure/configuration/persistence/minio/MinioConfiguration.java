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
import software.amazon.awssdk.services.s3.S3Configuration;

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
        String endpoint = String.format("%s:%d", minioProperties.getHost(), minioProperties.getPort());
        if (minioProperties.getSecure()) {
            endpoint = "https://" + endpoint;
        } else {
            endpoint = "http://" + endpoint;
        }

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                minioProperties.getAccessKey(),
                                minioProperties.getSecretKey()
                        )
                ))
                .region(Region.US_EAST_1)
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }

}