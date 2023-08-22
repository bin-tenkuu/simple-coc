package com.github.bin.command

import com.github.bin.service.CocService
import com.github.bin.service.HisMsgService.sendAsBot
import com.github.bin.service.RoomConfig
import com.github.bin.util.DiceResult

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */
object CocCommand {
    // d
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
            sendAsBot(roomConfig, "投掷 $num 次骰子，结果为 $result")
            return true
        }
    }

    // dall1
    class Dall1 : SimpleCommand("dall1") {
        override fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean {
            CocService.cheater = !CocService.cheater
            sendAsBot(roomConfig, "全1" + if (CocService.cheater) "开" else "关")
            return true
        }
    }

    // dp
    class Dp : SimpleCommand("dp") {
        override fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean {
            val num = msg.split(" ", limit = 1).let {
                if (it.isEmpty()) return true
                it[0].toIntOrNull() ?: return true
            }
            val roleId = roomConfig.getRole(id)
            var cacheResult: DiceResult = CocService.cache[roleId] ?: run {
                sendAsBot(roomConfig, "10分钟之内没有投任何骰子")
                return true
            }
            val dice = DiceResult(num, cacheResult.max)
            if (!CocService.cheater) dice.dice()
            cacheResult += dice
            CocService.cache[roleId] = cacheResult
            val msg = """${dice.origin}：[${dice.list.joinToString(", ")}]=${dice.sum}
                |[${cacheResult.list.joinToString(", ")}]
            """.trimMargin()
            sendAsBot(roomConfig, msg)
            return true
        }
    }

    // r
    class R : SimpleCommand("r") {
        private val diceRegex = Regex("""(?<num>\d*)d(?<max>\d*)""", RegexOption.IGNORE_CASE)

        override fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean {
            val split = msg.split(" ", limit = 2)
            if (split.size < 2) {
                return true
            }
            val dice = split[0]
            val type = split[1]
            val matchResult = diceRegex.find(dice) ?: run {
                sendAsBot(roomConfig, "骰子格式错误")
                return true
            }
            val groups = matchResult.groups
            val num = groups["num"]!!.value.toIntOrNull() ?: 1
            val max = groups["max"]!!.value.toIntOrNull() ?: 0
            val diceResult = DiceResult(num, max)
            if (!CocService.cheater) diceResult.dice()
            val roleId = roomConfig.getRole(id)
            val pre = (roomConfig.room.roles[roleId]?.name ?: "") + "进行" + type + "检定：\n"
            val msg = if (num == 1) {
                "${diceResult.origin} = ${diceResult.sum}"
            } else {
                "${diceResult.origin} = ${diceResult.list.joinToString("+")} = ${diceResult.sum}"
            }
            sendAsBot(roomConfig, pre + msg)
            return true
        }
    }
}
