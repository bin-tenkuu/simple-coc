package com.github.bin.service

import com.github.bin.entity.Room
import com.github.bin.model.Message
import com.github.bin.util.JsonUtil.toJson
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
class RoomConfig(
        val room: Room,
) : Closeable {
    private val clients = ConcurrentHashMap<String, WebSocketSession>()
    private val roles = HashMap<String, String>()
    operator fun plusAssign(session: WebSocketSession) {
        clients[session.id] = session
    }

    operator fun minusAssign(session: WebSocketSession) {
        clients.remove(session.id)
    }

    fun getRole(session: String): String {
        return roles[session] ?: ""
    }

    fun setRole(session: String, role: String) {
        roles[session] = role
    }

    fun sendAll(msg: Message) {
        val json = msg.toJson()
        for (client in clients.values) {
            client.sendMessage(TextMessage(json))
        }
    }

    override fun close() {
        for (client in clients.values) {
            client.close()
        }
        clients.clear()
    }
}
