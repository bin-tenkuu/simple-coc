package com.github.bin.config

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.HandshakeInterceptor


/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Component
class WebSocketConfig(
        val socketHandler: WebSocketHandler,
        val interceptor: HandshakeInterceptor,
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(socketHandler, "/ws/{roomId}")
                .addInterceptors(interceptor)
                .setAllowedOrigins("*")
    }
}
