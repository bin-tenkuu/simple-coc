package com.github.bin.model

import com.github.bin.config.Global
import com.github.bin.entity.Room
import jakarta.websocket.Session

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
class RoomConfig(
    val room: Room
) {
    val clients = ArrayList<Session>()
    fun sendAll(msg: Message) {
        val json = Global.toJson(msg)
        for (client in clients) {
            client.asyncRemote.sendText(json)
        }
    }
}
