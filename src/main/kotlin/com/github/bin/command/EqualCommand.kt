package com.github.bin.command

import com.github.bin.service.RoomConfig

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */
abstract class EqualCommand(protected val msg: String) : Command {
    override fun invoke(roomConfig: RoomConfig, id: String, msg: String): Boolean {
        if (this.msg == msg) {
            return invoke(roomConfig, id)
        }
        return false
    }

    abstract fun invoke(roomConfig: RoomConfig, id: String): Boolean
}
