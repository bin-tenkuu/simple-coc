package com.github.bin.config

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import com.github.bin.config.handler.MsgTableName
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Component
class DateSourceConfig {

    @Bean
    fun interceptor(): MybatisPlusInterceptor {
        return MybatisPlusInterceptor().apply {
            addInnerInterceptor(DynamicTableNameInnerInterceptor(null, MsgTableName))
            addInnerInterceptor(PaginationInnerInterceptor())
        }
    }
}
