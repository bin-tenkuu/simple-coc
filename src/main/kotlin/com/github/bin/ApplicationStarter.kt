package com.github.bin

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.web.socket.config.annotation.EnableWebSocket

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
@EnableWebSocket
class ApplicationStarter : ApplicationListener<ApplicationStartedEvent> {
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        println("启动成功")
    }
}

fun main(args: Array<String>) {
    runApplication<ApplicationStarter>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
