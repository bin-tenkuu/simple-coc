package com.github.bin.controller

import com.github.bin.model.Message
import com.github.bin.service.RoomConfig
import com.github.bin.service.RoomService
import com.github.bin.util.JsonUtil.toBean
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import java.io.IOException

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/6/25
 */
@Component
class ChatWebSocketHandler(
        var roomService: RoomService,
) : WebSocketHandler {
    private val logger = LoggerFactory.getLogger(ChatWebSocketHandler::class.java)

    /**
     * 建立连接
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val roomConfig = session.getRoom()
        roomConfig += session
        logger.info("'{}' 连接 room '{}' ", session.remoteAddr, roomConfig.id)
    }

    /**
     * 接收消息
     */
    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        if (message is TextMessage) {
            val roomConfig = session.getRoom()
            val payload = message.payload
            try {
                val msg = payload.toBean(Message::class)
                roomService.handleMessage(roomConfig, session.id, msg)
            } catch (e: IOException) {
                logger.warn("'{}' 消息格式错误: '{}'", session.remoteAddr, payload)
                session.close(CloseStatus(4000, "消息格式错误"))
                return
            }
        }
    }

    /**
     * 发生错误
     */
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        val roomConfig = session.getRoom()
        roomConfig -= session
        if (session.isOpen) {
            session.close(CloseStatus(4000, "服务器错误:${exception.message}"))
        }
        logger.warn("'{}' websocket 异常", session.remoteAddr, exception)
    }

    /**
     * 关闭连接
     */
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val roomConfig = session.getRoom()
        roomConfig -= session
        logger.info("'{}' 断开连接", session.remoteAddr)
    }

    /**
     * 是否支持发送部分消息
     */
    override fun supportsPartialMessages(): Boolean = false

    private fun WebSocketSession.getRoom(): RoomConfig {
        return RoomService[attributes["roomId"] as String]!!
    }

    private val WebSocketSession.remoteAddr get() = remoteAddress?.hostName ?: "unknown"
}
