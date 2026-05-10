package com.kitadevelopers.pos.common.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    // Spring Boot auto-configures RedisConnectionFactory and StringRedisTemplate
    // from spring.data.redis.* properties.
}

//    @Bean
//    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory){
//        return new StringRedisTemplate(factory);
//    }
