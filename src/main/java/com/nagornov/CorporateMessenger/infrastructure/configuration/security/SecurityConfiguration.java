package com.nagornov.CorporateMessenger.infrastructure.configuration.security;

import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomTraceFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.handler.CustomAccessDeniedHandler;
import com.nagornov.CorporateMessenger.infrastructure.security.handler.CustomAuthenticationEntryPoint;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomCorsFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomCsrfFilter;
import com.nagornov.CorporateMessenger.infrastructure.security.filter.CustomJwtFilter;
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
    private final CustomCsrfFilter csrfFilter;
    private final CustomJwtFilter jwtFilter;
    private final CustomTraceFilter traceFilter;

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
                    .requestMatchers("/chat").hasRole("USER") // for connect
                    .requestMatchers("/notifications").hasRole("USER") // for connect
                    
                    // TestController
                    .requestMatchers(HttpMethod.POST, "/api/test/1").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/test/2").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/test/3").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/test/4").permitAll()

                    // AuthController
                    .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/registration").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/logout").hasRole("USER")

                    // UserController
                    .requestMatchers(HttpMethod.PATCH, "/api/user/username").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/password").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/main-email").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/phone").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/search").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/block").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/unblock").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user").hasRole("USER")

                    // UserPhotoController
                    .requestMatchers(HttpMethod.POST, "/api/user/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/photo/main").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/photo/{photoId}").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/photo/{photoId}").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/photo/{photoId}").hasRole("USER")

                    // EmployeeController
                    .requestMatchers(HttpMethod.GET, "/api/user/employee").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/employee").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/employee").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/employee").hasRole("USER")

                    // NotificationController
                    .requestMatchers(HttpMethod.GET, "/api/user/notifications").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/notification/process").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/notification/read").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/notifications/read-all").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/notification").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/notifications").hasRole("USER")

                    // ContactController
                    .requestMatchers(HttpMethod.POST, "/api/user/contact").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/contact").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/contacts").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}/contacts").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/contact/confirm").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/user/contact/reject").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/user/contact").hasRole("USER")

                    // PrivateChatController
                    .requestMatchers(HttpMethod.POST, "/api/chat/private").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/private/all").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/private/{chatId}").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/api/chat/private/unavailable").hasRole("USER")

                    // GroupChatController
                    .requestMatchers(HttpMethod.POST, "/api/chat/group").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/all").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/group/{chatId}/metadata").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/group/{chatId}/status").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/chat/group/{chatId}/owner").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}").hasRole("USER")

                    // GroupChatMemberController
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/members").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/members/available").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/api/chat/group/{chatId}/members").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/members").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/leave").hasRole("USER")

                    // GroupChatPhotoController
                    .requestMatchers(HttpMethod.GET, "/api/chat/group/{chatId}/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/api/chat/group/{chatId}/photo").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/chat/group/{chatId}/photo").hasRole("USER")

                    // MessageController
                    .requestMatchers(HttpMethod.POST, "/api/message").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/message/{chatId}/{page}").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/message/content").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/message").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/api/message/read").hasRole("USER")
                    .requestMatchers(HttpMethod.GET, "/api/chat/{chatId}/message/{messageId}/file/{fileId}").hasRole("USER")

                    .anyRequest().authenticated()
            );
//            .exceptionHandling(exceptions -> exceptions
//                    .authenticationEntryPoint(authenticationEntryPoint)
//                    .accessDeniedHandler(accessDeniedHandler)
//            );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }
}