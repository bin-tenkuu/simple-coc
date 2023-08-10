package com.github.bin.command

import com.github.bin.service.HisMsgService
import com.github.bin.service.RoomConfig

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */
object CocCommand {
    class D : SimpleCommand("d") {
        private val diceRegex = Regex(
                """^(?:(?<times>\d+)#)?(?<dice>[+\-*d\d]+)""",
                RegexOption.IGNORE_CASE
        )

        override fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean {
            val split = msg.split(" ")
            if (split.size != 2) return false
            val num = split[1].toIntOrNull() ?: return false
            val result = (1..num).sumOf { (1..6).random() }
            HisMsgService.sendAsBot(roomConfig, "投掷 $num 次骰子，结果为 $result")
            return true
        }
    }
}
