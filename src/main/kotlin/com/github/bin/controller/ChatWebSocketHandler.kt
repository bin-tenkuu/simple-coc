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
        val roomConfig = session.attributes["room"] as RoomConfig
        roomConfig += session
    }

    /**
     * 接收消息
     */
    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        when (message) {
            is TextMessage -> {
                val roomConfig = session.attributes["room"] as RoomConfig
                val payload = message.payload
                try {
                    val msg = payload.toBean(Message::class)
                    roomService.handleMessage(roomConfig, session.id, msg)
                } catch (e: IOException) {
                    logger.info("消息格式错误: '{}'", payload)
                    session.close(CloseStatus(4000, "消息格式错误"))
                    return
                }
            }
        }
    }

    /**
     * 发生错误
     */
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        val roomConfig = session.attributes["room"] as RoomConfig
        roomConfig -= session
        if (session.isOpen) {
            session.close(CloseStatus(4000, "服务器错误:${exception.message}"))
        }
        logger.error("websocket 异常", exception)
    }

    /**
     * 关闭连接
     */
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val roomConfig = session.attributes["room"] as RoomConfig
        roomConfig -= session
    }

    /**
     * 是否支持发送部分消息
     */
    override fun supportsPartialMessages(): Boolean = false


}
