package com.nagornov.CorporateMessenger.infrastructure.configuration.db.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisJwtSessionEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisTemplateConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    @Bean(name = "redisMessageTemplate")
    public RedisTemplate<String, RedisMessageEntity> redisMessageTemplate() {
        RedisTemplate<String, RedisMessageEntity> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper customMapper = objectMapper.copy();
        customMapper.activateDefaultTyping(
            customMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(customMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "redisJwtSessionTemplate")
    public RedisTemplate<String, RedisJwtSessionEntity> redisJwtSessionTemplate() {
        RedisTemplate<String, RedisJwtSessionEntity> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper customMapper = objectMapper.copy();
        customMapper.activateDefaultTyping(
            customMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(customMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "redisCsrfTemplate")
    public RedisTemplate<String, String> redisCsrfTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
