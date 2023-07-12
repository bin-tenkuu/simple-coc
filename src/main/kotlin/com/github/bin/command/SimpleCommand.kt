package com.github.bin.command

import com.github.bin.service.RoomConfig

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */
abstract class SimpleCommand(
        private val first: String
) : Command {

    override fun invoke(roomConfig: RoomConfig, id: String, msg: String): Boolean {
        val split = test(msg) ?: return false
        return handler(roomConfig, id, split)
    }

    open fun test(split: String): String? {
        return if (split.startsWith(first)) split.substring(first.length) else null
    }

    abstract fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean
}
