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
    val id get() = room.id!!
    private val clients = ConcurrentHashMap<String, WebSocketSession>()
    private val roles = HashMap<String, Int>()

    @JvmField
    @Volatile
    @Transient
    var hold = true
    operator fun plusAssign(session: WebSocketSession) {
        clients[session.id] = session
        hold = true
    }

    operator fun minusAssign(session: WebSocketSession) {
        clients.remove(session.id)
        if (clients.isEmpty()) {
            hold = false
        }
    }

    fun getRole(session: String): Int {
        return roles.getOrDefault(session, -1)
    }

    fun setRole(session: String, role: Int) {
        roles[session] = role
    }

    fun sendAll(msg: Message) {
        val json = msg.toJson()
        for (client in clients.values) {
            client.sendMessage(TextMessage(json))
        }
    }

    fun send(id: String, msg: Message) {
        val json = msg.toJson()
        clients[id]?.sendMessage(TextMessage(json))
    }

    override fun close() {
        for (client in clients.values) {
            client.close()
        }
        clients.clear()
    }
}
