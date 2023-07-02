package com.github.bin.service

import com.github.bin.util.JsonUtil.toBean
import com.github.bin.util.JsonUtil.toJson
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/2
 */
@Service
class RedisService(
        redis: StringRedisTemplate,
) {
    // region Value
    private val forValue = redis.opsForValue()

    fun getValue(key: String): String? {
        return forValue.get(key)
    }

    fun <T : Any> getValue(key: String, clazz: Class<T>): T? {
        return forValue.get(key)?.toBean(clazz)
    }

    fun <T> setValue(key: String, value: T) {
        if (value == null) {
            return
        }
        forValue.set(key, value.toJson())
    }

    fun <T> setValue(key: String, value: T, duration: Duration) {
        if (value == null) {
            return
        }
        forValue.set(key, value.toJson(), duration)
    }

    // endregion
}
