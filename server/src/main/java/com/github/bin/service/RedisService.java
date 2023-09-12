package com.github.bin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redis;
    // region Value

    public String getValue(String key) {
        return redis.opsForValue().get(key);
    }

    public void setValue(String key, String value, Duration timeout) {
        redis.opsForValue().set(key, value, timeout);
    }

    // endregion
}
