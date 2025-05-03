package com.nagornov.CorporateMessenger.domain.client;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.common.LogRequest;
import com.nagornov.CorporateMessenger.domain.enums.client.LogClientEndpoint;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.LogServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.security.utils.UrlUtils;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LogClient {

    private final ServiceProperties serviceProperties;
    private final LogServiceProperties logServiceProperties;

    private static final RestTemplate restTemplate = new RestTemplate();


    public void sendLog(@NonNull Log log) {

        String uri = UrlUtils.normalizeUri(
                logServiceProperties.getUrl(), LogClientEndpoint.SEND_LOG.getEndpoint()
        );

        LogRequest request = new LogRequest(log);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", serviceProperties.getServerUrl());
        headers.set("Content-Type", "application/json");
        headers.set("X-Api-Key", logServiceProperties.getApiKey());
        headers.set("X-Trace-Id", log.getTraceId());

        HttpEntity<LogRequest> requestEntity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(uri, requestEntity, HttpResponse.class);
    }

}