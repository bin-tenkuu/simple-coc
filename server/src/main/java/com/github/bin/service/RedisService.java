package com.github.bin.service;

import com.github.bin.config.CustomRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
public final class RedisService {
    private static CustomRedisTemplate redis;

    @Autowired
    private void setRedisTemplate(CustomRedisTemplate redisTemplate) {
        RedisService.redis = redisTemplate;
    }

    public static void remove(String key) {
        redis.unlink(key);
    }

    // region Value
    @SuppressWarnings("unchecked")
    private static <V> ValueOperations<String, V> opsForValue(Class<V> clazz) {
        redis.setTargetType(clazz);
        return (ValueOperations<String, V>) redis.opsForValue();
    }

    public static String getValue(String key) {
        return opsForValue(String.class).get(key);
    }

    public static <T> T getValue(String key, Class<T> clazz) {
        return opsForValue(clazz).get(key);
    }

    public static <T> T getValue(String key, Class<T> clazz, Duration timeout) {
        return opsForValue(clazz).getAndExpire(key, timeout);
    }

    public static <T> void setValue(String key, T value) {
        redis.opsForValue().set(key, value);
    }

    public static <T> void setValue(String key, T value, Duration timeout) {
        redis.opsForValue().set(key, value, timeout);
    }

    // endregion

    // region hash
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(T value) {
        return (Class<T>) value.getClass();
    }

    private static <V> HashOperations<String, String, V> opsForHash(Class<V> clazz) {
        redis.setTargetType(clazz);
        return redis.opsForHash();
    }

    public static String getHash(String key, String hashKey) {
        return opsForHash(String.class).get(key, hashKey);
    }

    public static <T> T getHash(String key, String hashKey, Class<T> clazz) {
        return opsForHash(clazz).get(key, hashKey);
    }

    public static <T> void setHash(String key, String hashKey, T value) {
        if (value == null) {
            opsForHash(null).delete(key, hashKey);
        } else {
            opsForHash(getClass(value)).put(key, hashKey, value);
        }
    }

    public static Map<String, String> getHash(String key) {
        return opsForHash(String.class).entries(key);
    }

    public static void removeHash(String key, String value) {
        opsForHash(String.class).delete(key, value);
    }

    // endregion

}
