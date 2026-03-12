package com.github.bin.service;

import com.github.bin.util.CacheMap;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
public final class RedisService {
    private static final CacheMap<String, Object> redis = new CacheMap<>();

    public static void remove(String key) {
        redis.remove(key);
    }

    // region Value
    @SuppressWarnings("unchecked")
    private static <V> CacheMap<String, V> opsForValue(Class<V> clazz) {
        return (CacheMap<String, V>) redis;
    }

    public static String getValue(String key) {
        return opsForValue(String.class).get(key);
    }

    public static <T> T getValue(String key, Class<T> clazz) {
        return opsForValue(clazz).get(key);
    }

    public static <T> T getValue(String key, Class<T> clazz, Duration timeout) {
        return opsForValue(clazz).getAndExpire(key, timeout.toMillis());
    }

    public static <T> void setValue(String key, T value) {
        redis.set(key, value);
    }

    public static <T> void setValue(String key, T value, Duration timeout) {
        redis.set(key, value, timeout.toMillis());
    }

    // endregion

    // region hash
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(T value) {
        return (Class<T>) value.getClass();
    }

    @SuppressWarnings("unchecked")
    private static <V> CacheMap<String, V> opsForHash(Class<V> clazz) {
        return (CacheMap<String, V>) redis;
    }

    public static String getHash(String key, String hashKey) {
        return opsForHash(String.class).get(key + ":" + hashKey);
    }

    public static <T> void setHash(String key, String hashKey, T value) {
        if (value == null) {
            opsForHash(null).remove(key + ":" + hashKey);
        } else {
            opsForHash(getClass(value)).set(key + ":" + hashKey, value);
        }
    }

    public static void removeHash(String key, String value) {
        opsForHash(String.class).remove(key + ":" + value);
    }

    // endregion

}
