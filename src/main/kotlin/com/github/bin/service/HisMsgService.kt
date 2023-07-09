package com.github.bin.service

import com.github.bin.command.Command
import com.github.bin.config.handler.MsgTableName
import com.github.bin.mapper.HisMsgMapper
import com.github.bin.model.Message
import org.springframework.stereotype.Service

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */
@Suppress("LeakingThis")
@Service
class HisMsgService(
        private val hisMsgMapper: HisMsgMapper,
        private val commands: List<Command>
) {
    init {
        INSTANCE = this
    }

    fun <T> invoke(tableName: String, block: HisMsgMapper.() -> T): T {
        MsgTableName.set(tableName)
        try {
            return hisMsgMapper.block()
        } finally {
            MsgTableName.remove()
        }
    }

    fun handleBot(roomConfig: RoomConfig, id: String, msg: String) {
        for (command in commands) {
            if (command.invoke(roomConfig, id, msg)) {
                break
            }
        }
    }

    companion object {
        private lateinit var INSTANCE: HisMsgService
        fun sendAsBot(room: RoomConfig, msg: String) {
            val text = Message.Text()
            text.msg = msg
            sendAsBot(room, text)
        }

        fun sendAsBot(room: RoomConfig, msg: Message.Msg) {
            msg.role = -10
            INSTANCE.invoke(room.id) {
                msg.id = insert(msg.type, msg.msg, -10)
            }
            room.sendAll(msg)
        }
    }
}
