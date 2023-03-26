package com.github.bin.util

import kotlinx.serialization.json.Json

/**
 *  @Date:2023/3/11
 *  @author bin
 *  @version 1.0.0
 */
val jsonGlobal = Json {
    classDiscriminator = "type"
    encodeDefaults = false
    ignoreUnknownKeys = true
    isLenient = false
    allowStructuredMapKeys = false
    prettyPrint = false
    coerceInputValues = true
    useArrayPolymorphism = false
    allowSpecialFloatingPointValues = true
    useAlternativeNames = true
}
