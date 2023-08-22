package com.github.bin.service

import com.github.bin.command.Command
import com.github.bin.config.MsgDataSource
import com.github.bin.mapper.msg.HisMsgMapper
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
        MsgDataSource.set(tableName)
        return hisMsgMapper.block()
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
        fun RoomConfig.sendAsBot(msg: String) {
            val text = Message.Text()
            text.msg = msg
            sendAsBot(text)
        }

        private fun RoomConfig.sendAsBot(msg: Message.Msg) {
            msg.role = -10
            INSTANCE.invoke(id) {
                msg.id = insert(msg.type, msg.msg, -10)
            }
            sendAll(msg)
        }
    }
}
