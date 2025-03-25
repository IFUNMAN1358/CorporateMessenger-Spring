package com.nagornov.CorporateMessenger.infrastructure.configuration.db.redis;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class RedisConnectionConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();

        redisConfig.setHostName(redisProperties.getHost());
        redisConfig.setPort(redisProperties.getPort());
        redisConfig.setPassword(
                RedisPassword.of(redisProperties.getPassword())
        );

        return new LettuceConnectionFactory(redisConfig);
    }
}