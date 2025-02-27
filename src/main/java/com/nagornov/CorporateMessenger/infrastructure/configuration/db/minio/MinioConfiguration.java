package com.nagornov.CorporateMessenger.infrastructure.configuration.db.minio;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}