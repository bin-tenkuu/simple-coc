package com.github.bin.config

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.bin.constant.DateConstant.DATE_FORMATTER
import com.github.bin.constant.DateConstant.DATE_TIME_FORMATTER
import com.github.bin.constant.DateConstant.TIME_FORMATTER
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author bin
 * @since 2022/12/28
 */
@Component
class CommonJacksonBuilderCustomizer : Jackson2ObjectMapperBuilderCustomizer {
    override fun customize(jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder) {
        jacksonObjectMapperBuilder
            .modules(
                KotlinModule.Builder()
                    .enable(KotlinFeature.NullIsSameAsDefault)
                    .enable(KotlinFeature.SingletonSupport)
                    .build()
            )
            .serializationInclusion(Include.NON_NULL)
            .featuresToDisable(
                DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
            )
            .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .serializerByType(Long::class.java, ToStringSerializer.instance)
            .serializerByType(java.lang.Long.TYPE, ToStringSerializer.instance)
            .serializerByType(LocalDateTime::class.java, LocalDateTimeSerializer(DATE_TIME_FORMATTER))
            .serializerByType(LocalDate::class.java, LocalDateSerializer(DATE_FORMATTER))
            .serializerByType(LocalTime::class.java, LocalTimeSerializer(TIME_FORMATTER))
            .deserializerByType(LocalDateTime::class.java, LocalDateTimeDeserializer(DATE_TIME_FORMATTER))
            .deserializerByType(LocalDate::class.java, LocalDateDeserializer(DATE_FORMATTER))
            .deserializerByType(LocalTime::class.java, LocalTimeDeserializer(TIME_FORMATTER))
    }
}
