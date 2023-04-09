@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.bin.constant

import java.time.format.DateTimeFormatter

/**
 * @author bin
 * @since 2022/12/28
 */
object DateConstant {
    /**
     * 默认时间格式
     */
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm:ss"
    const val DATE_TIME_FORMAT = "$DATE_FORMAT $TIME_FORMAT"
    const val SIMPLE_DATE_FORMAT = "yyyyMMdd"
    const val SIMPLE_TIME_FORMAT = "HHmmss"
    const val SIMPLE_DATE_TIME_FORMAT = SIMPLE_DATE_FORMAT + SIMPLE_TIME_FORMAT

    /**
     * 默认时间格式化
     */
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
    val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT)
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    val SIMPLE_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT)
    val SIMPLE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(SIMPLE_TIME_FORMAT)
    val SIMPLE_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(SIMPLE_DATE_TIME_FORMAT)

}
