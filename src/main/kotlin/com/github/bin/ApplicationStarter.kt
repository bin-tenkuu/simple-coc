package com.github.bin

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.mybatis.spring.annotation.MapperScan
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.socket.config.annotation.EnableWebSocket
import kotlin.reflect.KProperty

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@OpenAPIDefinition(
    info = Info(
        title = "Demo 接口",
        description = "Demo 相关 API",
        version = "v1.0"
    )
)
@SpringBootApplication
@MapperScan("com.github.bin.mapper")
class ApplicationStarter

fun main(args: Array<String>) {
    runApplication<ApplicationStarter>(*args)
}
