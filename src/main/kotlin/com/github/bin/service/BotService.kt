package com.github.bin.service

import com.github.bin.controller.WebSocketHandler
import com.github.bin.handler.BotHandler
import com.github.bin.model.RoomConfig
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

/**
 * @author bin
 * @since 2023/04/12
 */
@Service
class BotService(
        private val botHandlers: List<BotHandler>
) : InitializingBean {
    override fun afterPropertiesSet() {
        WebSocketHandler.botService = this
    }

    fun handler(room: RoomConfig, msg: String, role: String) {
        for (handler in botHandlers) {
            handler.handler(room, msg, role)
        }
    }
}
