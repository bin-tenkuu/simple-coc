package com.github.bin.config

import com.github.bin.service.RoomConfig
import com.github.bin.service.RoomService
import org.slf4j.LoggerFactory
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class WebSocketInterceptor(
        val roomService: RoomService,
) : HandshakeInterceptor {
    private val logger = LoggerFactory.getLogger(WebSocketInterceptor::class.java)
    override fun beforeHandshake(request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Boolean {
        if (request is ServletServerHttpRequest) {
            val roomId = request.uri.path.substring(4)
            val remoteHost = request.servletRequest.remoteAddr
            var config = RoomService[roomId]
            if (config == null) {
                val room = roomService.getById(roomId)
                if (room == null) {
                    logger.warn("'{}' 尝试连接 room '{}' （不存在）", remoteHost, roomId)
                    return false
                }
                config = RoomConfig(room)
                RoomService[roomId] = config
            } else {
                config.hold = true
            }
            attributes["roomId"] = roomId
            return true
        }
        return false
    }

    override fun afterHandshake(request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, exception: Exception?) {
    }
}
