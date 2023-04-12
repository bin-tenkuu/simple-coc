package com.github.bin.config

import com.github.bin.controller.WebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.socket.server.standard.ServerEndpointExporter


/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Component
class WebSocketConfig {
    @Bean
    fun serverEndpointExporter(): ServerEndpointExporter {
        return ServerEndpointExporter().apply {
            setAnnotatedEndpointClasses(WebSocketHandler::class.java)
        }
    }
}
