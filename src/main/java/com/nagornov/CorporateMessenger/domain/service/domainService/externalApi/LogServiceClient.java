package com.nagornov.CorporateMessenger.domain.service.domainService.externalApi;

import com.nagornov.CorporateMessenger.domain.enums.externalApi.LogServiceEndpoint;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class LogServiceClient {

    @Value("${log-service.url}")
    private String logServiceUrl;

    @Value("${log-service.api-key}")
    private String logServiceApiKey;

    private RestClient logServiceClient;

    @PostConstruct
    private void initLogServiceClient() {
        this.logServiceClient = RestClient.builder()
            .requestFactory(new HttpComponentsClientHttpRequestFactory())
            .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()))
            .baseUrl(logServiceUrl)
            .defaultHeader("X-Api-Key", logServiceApiKey)
            .build();
    }

    public void sendLog(@NotNull Log log) {
        this.logServiceClient.post()
                .uri(
                        normalizeUri(logServiceUrl, LogServiceEndpoint.SEND_LOG)
                )
                .header("X-Trace-Id", log.getTraceId())
                .body(new LogRequest(log))
                .retrieve()
                .toBodilessEntity();
    }

    private String normalizeUri(@NotNull String logServiceUrl, @NotNull LogServiceEndpoint logServiceEndpoint) {
        return URI.create(logServiceUrl).resolve(logServiceEndpoint.getEndpoint()).toString();
    }

    @Data
    @AllArgsConstructor
    private static class LogRequest {
        private Log log;
    }

}
