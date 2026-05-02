package com.kitadevelopers.pos.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "blacklist:";

    public void blacklist(String token, long ttlMillis){
        redisTemplate.opsForValue()
                .set(PREFIX + token, "1", ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}
//@Service
//@RequiredArgsConstructor
//public class JwtBlacklistService {
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    private static final String PREFIX = "blacklist:";
//
//    public void blacklist(String token, long ttlMillis){
//        redisTemplate.opsForValue()
//                .set(PREFIX + token, "1", ttlMillis, TimeUnit.MILLISECONDS);
//    }
//
//    public boolean isBlacklisted(String token){
//        return redisTemplate.hasKey(PREFIX + token);
//    }
//}
