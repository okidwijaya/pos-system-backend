package com.kitadevelopers.pos.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTestService {
    private final StringRedisTemplate redisTemplate;

    public void test(){
        redisTemplate.opsForValue().set("test","hello", Duration.ofMinutes(1));
        String val = redisTemplate.opsForValue().get("test");
        System.out.println("REDIS" + val);
    }
}
