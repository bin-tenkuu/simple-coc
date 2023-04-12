package com.github.bin.service

import com.github.bin.handler.BotHandler
import com.github.bin.model.Message
import com.github.bin.model.RoomConfig
import org.springframework.stereotype.Service

/**
 * @author bin
 * @since 2023/04/12
 */
@Service
class BotService(
    private val botHandlers: List<BotHandler>
) {

    fun handler(service: RoomService, room: RoomConfig, msg: String, role: String) {
        for (handler in botHandlers) {
            val str = handler.handler(room, msg, role)
            if (str != null) {
                service.saveMsgAndSend(room, Message.Text(str), "bot")
            }
        }
    }
}
