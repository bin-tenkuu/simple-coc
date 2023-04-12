package com.github.bin.handler

import com.github.bin.model.RoomConfig

/**
 * @author bin
 * @since 2023/04/12
 */
interface BotHandler {
    fun handler(room: RoomConfig, msg: String, role: String)
}
