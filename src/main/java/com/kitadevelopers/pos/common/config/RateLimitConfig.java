package com.kitadevelopers.pos.common.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip){
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip){
        Bandwidth limit = Bandwidth.simple(5, Duration.ofMinutes(1));
        return Bucket.builder().addLimit(limit).build();
    }

    public boolean tryConsume(String ip){
        return resolveBucket(ip).tryConsume(1);
    }
}
