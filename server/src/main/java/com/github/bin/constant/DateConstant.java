package com.github.bin.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author bin
 * @since 2023/08/22
 */
public interface DateConstant {
    /**
     * 默认时间格式
     */
    String DATE_FORMAT = "yyyy-MM-dd";
    String TIME_FORMAT = "HH:mm:ss";
    String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    /**
     * 默认时间格式化
     */
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

}
