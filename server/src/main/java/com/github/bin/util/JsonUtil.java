package com.github.bin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author bin
 * @since 2023/05/30
 */
@Component
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    @Autowired
    public void setObjectMapper(Jackson2ObjectMapperBuilder builder) {
        builder.configure(MAPPER);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return MAPPER;
    }

    public static <T> String toJson(T value) {
        if (value == null) {
            return "";
        }
        if (value instanceof CharSequence) {
            return value.toString();
        }
        try {
            return MAPPER.writeValueAsString(value);
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
            return MAPPER.writeValueAsBytes(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> toMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toBean(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
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
        return MAPPER.readValue(json, clazz);
    }

    public static <T> T toBean(String json, TypeReference<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(String json, JavaType clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(byte[] json, JavaType clazz) {
        if (json == null || json.length == 0) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toBeanList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            val type = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertBean(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.convertValue(obj, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertBean(Object obj, TypeReference<T> clazz) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.convertValue(obj, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertBean(Object obj, JavaType clazz) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.convertValue(obj, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JavaType getJavaType(Type type) {
        return MAPPER.getTypeFactory().constructType(type);
    }

    public static JavaType getListJavaType(Class<?> type) {
        return MAPPER.getTypeFactory().constructCollectionType(List.class, type);
    }

    public static JavaType getMapJavaType(Class<?> key, Class<?> value) {
        return MAPPER.getTypeFactory().constructMapType(Map.class, key, value);
    }

    public static <T> JavaType getJavaType(TypeReference<T> type) {
        return MAPPER.getTypeFactory().constructType(type);
    }
}
