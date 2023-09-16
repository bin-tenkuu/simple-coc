package com.github.bin.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.github.bin.util.JsonUtil;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.lang.constant.ConstantDesc;
import java.lang.reflect.Type;

/**
 * @author bin
 * @since 2023/09/12
 */
@Component
public class CustomRedisTemplate extends RedisTemplate<String, Object> {
    public CustomRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        setConnectionFactory(connectionFactory);
        val valueSerializer = new CustomRedisSerializer();
        setDefaultSerializer(StringRedisSerializer.UTF_8);
        setKeySerializer(StringRedisSerializer.UTF_8);
        setValueSerializer(valueSerializer);
        setHashKeySerializer(StringRedisSerializer.UTF_8);
        setHashValueSerializer(valueSerializer);
    }

    public void setTargetType(Type type) {
        CustomRedisSerializer.CLASS_CACHE.set(JsonUtil.getJavaType(type));
    }

    public void setTargetType(TypeReference<?> type) {
        CustomRedisSerializer.CLASS_CACHE.set(JsonUtil.getJavaType(type));
    }

    /**
     * 支持一下自定义序列化，防止值的二次序列化
     */
    private static class CustomRedisSerializer implements RedisSerializer<Object> {
        private static final StringRedisSerializer STRING = StringRedisSerializer.UTF_8;
        private static final ThreadLocal<JavaType> CLASS_CACHE = new ThreadLocal<>();

        @Override
        public byte[] serialize(Object o) throws SerializationException {
            if (o == null) {
                return null;
            } else if (o instanceof CharSequence || o instanceof ConstantDesc) {
                return STRING.serialize(o.toString());
            } else if (o instanceof byte[] bytes) {
                return bytes;
            } else {
                return JsonUtil.toJsonBytes(o);
            }
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            JavaType type = CLASS_CACHE.get();
            if (type == null) {
                return STRING.deserialize(bytes);
            } else if (type.isTypeOrSubTypeOf(byte[].class)) {
                return bytes;
            } else {
                return JsonUtil.toBean(bytes, type);
            }
        }

        @Override
        public boolean canSerialize(@NotNull Class<?> type) {
            return true;
        }

        @Override
        public @NotNull Class<?> getTargetType() {
            return Object.class;
        }
    }
}
