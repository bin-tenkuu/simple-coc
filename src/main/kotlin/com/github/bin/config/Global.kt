package com.github.bin.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.bin.config.handler.BaseJsonTypeHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Component
class Global {
    companion object {
        lateinit var objectMapper: ObjectMapper
        fun toJson(obj: Any): String {
            return objectMapper.writeValueAsString(obj)
        }

        fun <T : Any> fromJson(json: String, typeReference: TypeReference<T>): T {
            return objectMapper.readValue(json, typeReference)
        }

        inline fun <reified T : Any> fromJson(json: String): T {
            return fromJson(json, object : TypeReference<T>() {})
        }
    }

    @Autowired
    fun setter(
        objectMapper: ObjectMapper,
    ) {
        Global.objectMapper = objectMapper
        BaseJsonTypeHandler.objectMapper = objectMapper
    }
}
