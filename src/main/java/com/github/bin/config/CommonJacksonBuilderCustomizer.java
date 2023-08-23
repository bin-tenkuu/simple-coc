package com.github.bin.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.github.bin.constant.DateConstant;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
public class CommonJacksonBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(
                        DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                )
                .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(java.lang.Long.TYPE, ToStringSerializer.instance)
                .serializerByType(LocalDateTime.class,
                        new LocalDateTimeSerializer(DateConstant.DATE_TIME_FORMATTER))
                .serializerByType(LocalDate.class,
                        new LocalDateSerializer(DateConstant.DATE_FORMATTER))
                .serializerByType(LocalTime.class,
                        new LocalTimeSerializer(DateConstant.TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class,
                        new LocalDateTimeDeserializer(DateConstant.DATE_TIME_FORMATTER))
                .deserializerByType(LocalDate.class,
                        new LocalDateDeserializer(DateConstant.DATE_FORMATTER))
                .deserializerByType(LocalTime.class,
                        new LocalTimeDeserializer(DateConstant.TIME_FORMATTER));
    }
}
