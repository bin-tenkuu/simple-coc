package com.github.bin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author bin
 * @since 2023/05/30
 */
@Component
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        JsonUtil.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public static <T> String toJson(T value) {
        if (value == null) {
            return "";
        }
        if (value instanceof CharSequence) {
            return value.toString();
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> byte[] toJsonBytes(T value) {
        if (value == null) {
            return new byte[0];
        }
        if (value instanceof CharSequence) {
            return value.toString().getBytes();
        }
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> toMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toBean(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T toBean(String json, Class<T> clazz) throws JsonProcessingException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return (T) json;
        }
        return objectMapper.readValue(json, clazz);
    }

    public static <T> T toBean(String json, TypeReference<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(String json, JavaType clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(byte[] json, JavaType clazz) {
        if (json == null || json.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toBeanList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            val type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertBean(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertBean(Object obj, TypeReference<T> clazz) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertBean(Object obj, JavaType clazz) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JavaType getJavaType(Type type) {
        return objectMapper.getTypeFactory().constructType(type);
    }

    public static JavaType getListJavaType(Class<?> type) {
        return objectMapper.getTypeFactory().constructCollectionType(List.class, type);
    }

    public static JavaType getMapJavaType(Class<?> key, Class<?> value) {
        return objectMapper.getTypeFactory().constructMapType(Map.class, key, value);
    }

    public static <T> JavaType getJavaType(TypeReference<T> type) {
        return objectMapper.getTypeFactory().constructType(type);
    }
}
