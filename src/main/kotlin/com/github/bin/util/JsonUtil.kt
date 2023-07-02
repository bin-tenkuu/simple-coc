package com.github.bin.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/6/24
 */
object JsonUtil {

    private lateinit var objectMapper: ObjectMapper

    fun Any.toJson(): String {
        if (this is CharSequence) {
            return toString()
        }
        return objectMapper.writeValueAsString(this)
    }

    @Throws(IOException::class)
    fun <T : Any> String.toBean(type: Type): T {
        val javaType = objectMapper.typeFactory.constructType(type)
        return objectMapper.readValue(this, javaType)
    }

    @Throws(IOException::class)
    fun <T : Any> String.toBean(kClass: KClass<T>): T {
        return objectMapper.readValue(this, kClass.java)
    }

    @Throws(IOException::class)
    fun <T> String.toBean(typeReference: TypeReference<T>): T {
        return objectMapper.readValue(this, typeReference)
    }

    @Component
    class JsonUtilInject {
        @Autowired
        fun setObjectMapper(objectMapper: ObjectMapper) {
            JsonUtil.objectMapper = objectMapper
        }
    }

}
