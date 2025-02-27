package com.nagornov.CorporateMessenger.domain.service.domainService.externalApi;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.common.LogRequest;
import com.nagornov.CorporateMessenger.domain.enums.externalApi.LogServiceEndpoint;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.UrlUtils;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.properties.LogProperties;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LogServiceClient {

    private final ServiceProperties serviceProperties;
    private final LogProperties logProperties;

    private static final RestTemplate restTemplate = new RestTemplate();


    public void sendLog(@NotNull Log log) {

        String uri = UrlUtils.normalizeUri(
                logProperties.getUrl(), LogServiceEndpoint.SEND_LOG.getEndpoint()
        );

        LogRequest request = new LogRequest(log);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", serviceProperties.getServerUrl());
        headers.set("Content-Type", "application/json");
        headers.set("X-Api-Key", logProperties.getApiKey());
        headers.set("X-Trace-Id", log.getTraceId());

        HttpEntity<LogRequest> requestEntity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(uri, requestEntity, HttpResponse.class);
    }

}