package com.github.bin.aspect;

import com.github.bin.service.RedisService;
import com.github.bin.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/2
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAspect {
    private final RedisService redis;

    @Around("@annotation(redisValue)")
    public Object around(ProceedingJoinPoint pjp, RedisValue redisValue) throws Throwable {
        if (pjp.getSignature() instanceof MethodSignature methodSignature) {
            val type = methodSignature.getMethod().getGenericReturnType();
            val value = redis.getValue(redisValue.key());
            if (value != null) {
                try {
                    return JsonUtil.toBean(value, JsonUtil.getJavaType(type));
                } catch (Exception e) {
                    log.warn("反序列化失败：type：{}，json：{}", type, value);
                }
            }
        }
        val result = pjp.proceed();
        val json = JsonUtil.toJson(result);
        val timeout = Duration.of(redisValue.expire(), redisValue.timeUnit().toChronoUnit());
        redis.setValue(redisValue.key(), json, timeout);
        return result;
    }
}
