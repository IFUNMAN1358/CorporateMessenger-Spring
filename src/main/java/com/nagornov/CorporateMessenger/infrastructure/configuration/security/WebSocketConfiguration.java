package com.nagornov.CorporateMessenger.infrastructure.configuration.security;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.JwtChannelInterceptor;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.JwtHandshakeInterceptor;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final ServiceProperties serviceProperties;
    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setAllowedOrigins(serviceProperties.getClientUrl())
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();
        registry.addEndpoint("/notifications")
                .setAllowedOrigins(serviceProperties.getClientUrl())
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}