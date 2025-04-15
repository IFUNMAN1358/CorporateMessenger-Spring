package com.nagornov.CorporateMessenger.infrastructure.configuration.security;

import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomTraceFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.handler.CustomAccessDeniedHandler;
import com.nagornov.CorporateMessenger.infrastructure.security.handler.CustomAuthenticationEntryPoint;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomCorsFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomCsrfFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomJwtFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.manager.SessionAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomCorsFilter corsFilter;
    private final CustomCsrfFilter csrfFilter;
    private final CustomJwtFilter jwtFilter;
    private final CustomTraceFilter traceFilter;

    private final SessionAuthorizationManager hasSession;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http
            .httpBasic(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                    .contentSecurityPolicy(csp -> csp
                            .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; object-src 'none';")
                    )
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
            .addFilterBefore(csrfFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(traceFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth

                    // WEBSOCKET
                    .requestMatchers("/ws-chat/**").permitAll()
                    
                    // TestController
                    .requestMatchers(HttpMethod.POST, "/api/test/1").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/test/2").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/test/3").permitAll()

                    // AuthController
                    .requestMatchers(HttpMethod.POST, "/api/auth/registration").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/logout").access(allOf(hasRole("USER"), hasSession))

                    // UserController
                    .requestMatchers(HttpMethod.POST, "/api/user/phone").access(allOf(hasRole("USER"), hasSession)) /////////
                    .requestMatchers(HttpMethod.POST, "/api/user/main-email").access(allOf(hasRole("USER"), hasSession)) /////////
                    .requestMatchers(HttpMethod.PATCH, "/api/user/password").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/user/search").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/user").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.DELETE, "/api/user").access(allOf(hasRole("USER"), hasSession))

                    // UserPhotoController
                    .requestMatchers(HttpMethod.POST, "/api/user/photo").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/photo/main").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/user/photo/{photoId}").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.PATCH, "/api/user/photo/{photoId}").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.DELETE, "/api/user/photo/{photoId}").access(allOf(hasRole("USER"), hasSession))

                    // PrivateChatController
                    .requestMatchers(HttpMethod.POST, "/api/chat/private").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/chat/private/all").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/chat/private/{chatId}").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.POST, "/api/chat/private/unavailable").access(allOf(hasRole("USER"), hasSession))

                    // GroupChatController
                    .requestMatchers(HttpMethod.POST, "/api/chat/group").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/all").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/group/{chatId}/metadata").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/group/{chatId}/status").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/group/{chatId}/owner").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}").access(allOf(hasRole("USER"), hasSession))

                    // GroupChatMemberController
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/members").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/members/available").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.POST, "/api/chat/group/{chatId}/members").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/members").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/leave").access(allOf(hasRole("USER"), hasSession))

                    // GroupChatPhotoController
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/photo").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.POST, "/api/chat/group/{chatId}/photo").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/photo").access(allOf(hasRole("USER"), hasSession))

                    // MessageController
                    .requestMatchers(HttpMethod.POST, "/api/message").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/message/{chatId}/{page}").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.PATCH, "/api/message/content").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.DELETE, "/api/message").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.POST, "/api/message/read").access(allOf(hasRole("USER"), hasSession))
                    .requestMatchers(HttpMethod.GET, "/api/chat/{chatId}/message/{messageId}/file/{fileId}").access(allOf(hasRole("USER"), hasSession))

                    .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }
}