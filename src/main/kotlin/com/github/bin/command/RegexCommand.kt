package com.github.bin.command

import com.github.bin.service.RoomConfig

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */
abstract class RegexCommand(
        private val regex: Regex
) : Command {
    override fun invoke(roomConfig: RoomConfig, id: String, msg: String): Boolean {
        val result = test(msg) ?: return false
        return handler(roomConfig, id, result)
    }

    open fun test(msg: String): MatchResult? {
        return regex.find(msg)
    }

    abstract fun handler(roomConfig: RoomConfig, id: String, msg: MatchResult): Boolean
}
