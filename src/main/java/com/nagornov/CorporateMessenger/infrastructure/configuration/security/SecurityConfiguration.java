package com.nagornov.CorporateMessenger.infrastructure.configuration.security;

import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomTraceFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomCorsFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomSessionFilter;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomCorsFilter corsFilter;
    private final CustomSessionFilter sessionFilter;
    private final CustomTraceFilter traceFilter;


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
            .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(traceFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth

                    // WEBSOCKET
                    .requestMatchers("/api/ws/chat/**").permitAll() // for connect
                    .requestMatchers("/api/ws/notifications/**").permitAll() // for connect
                    
                    // TestController
                    .requestMatchers(HttpMethod.POST, "/api/test/1").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/test/2").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/test/3").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/test/4").permitAll()

                    // ExternalAuthController
                    .requestMatchers(HttpMethod.POST, "/api/external/v1/auth/login").permitAll()

                    // AuthController
                    .requestMatchers(HttpMethod.POST, "/api/auth/registration").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/logout").hasRole("USER")

                    // ExternalServiceController
                    .requestMatchers(HttpMethod.POST, "/api/external-service").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/external-service/{serviceName}").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/external-services").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/external-service/{serviceName}").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/external-service/{serviceName}").hasRole("ADMIN")

                    // RegistrationKeyController
                    .requestMatchers(HttpMethod.POST, "/api/registration-key").hasRole("ADMIN")
                    //.requestMatchers(HttpMethod.GET, "/api/registration-keys").hasRole("ADMIN") not used
                    //.requestMatchers(HttpMethod.DELETE, "/api/registration-key/{keyId}").hasRole("ADMIN") not used

                    // UserController
                    .requestMatchers(HttpMethod.PATCH, "/api/user/firstName-and-lastName").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/username").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/password").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/bio").hasRole("USER")
                    //.requestMatchers(HttpMethod.PATCH, "/api/user/main-email").hasRole("USER") not used
                    //.requestMatchers(HttpMethod.PATCH, "/api/user/phone").hasRole("USER") not used
                    .requestMatchers(HttpMethod.GET, "/api/user/search").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/exists-username").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/user").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/blocked").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/{userId}/block").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/{userId}/unblock").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user").hasRole("USER")

                    // UserSettingsController
                    .requestMatchers(HttpMethod.GET, "/api/user/settings").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/settings").hasRole("USER")

                    // UserPhotoController
                    .requestMatchers(HttpMethod.POST, "/api/user/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/photo/main").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/photo/{photoId}").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/photo/main").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/photo/{photoId}").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/photo/{photoId}").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/photo/{photoId}").hasRole("USER")

                    // EmployeeController
                    .requestMatchers(HttpMethod.GET, "/api/user/employee").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/employee").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/employee").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/employee").hasRole("USER")

                    // EmployeePhotoController
                    .requestMatchers(HttpMethod.POST, "/api/user/employee/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/employee/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/employee/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/employee/photo").hasRole("USER")

                    // NotificationController
                    .requestMatchers(HttpMethod.GET, "/api/user/notifications").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/notification/{notificationId}/process").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/notification/{notificationId}/read").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/notifications/read-all").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/notification/{notificationId}").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/notifications").hasRole("USER")

                    // ContactController
                    .requestMatchers(HttpMethod.POST, "/api/user/{userId}/contact").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/contact").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/contacts").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/contacts").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/{userId}/contact/confirm").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/{userId}/contact/reject").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/{userId}/contact").hasRole("USER")

                    // ChatController
                    .requestMatchers(HttpMethod.POST, "/api/user/{userId}/chat/private").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/api/chat/group").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/exists-username").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/{chatId}").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chats").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/{chatId}/group/title").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/{chatId}/group/username").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/{chatId}/group/description").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/{chatId}/group/settings").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/{chatId}").hasRole("USER")

                    // ChatMemberController
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/members").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/members/available").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/api/chat/group/{chatId}/members").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/members").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/leave").hasRole("USER")

                    // ChatPhotoController
                    .requestMatchers(HttpMethod.POST, "/api/chat/group/{chatId}/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/photos").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/photo/main").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/photo/{photoId}").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/group/{chatId}/photo/{photoId}").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/photo/{photoId}").hasRole("USER")

                    // MessageController
                    .requestMatchers(HttpMethod.POST, "/api/chat/{chatId}/message").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/{chatId}/messages").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/{chatId}/message/{messageId}/file/{fileId}").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/{chatId}/message/{messageId}/read").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/{chatId}/message/{messageId}/content").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/{chatId}/message/{messageId}").hasRole("USER")

                    .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }
}