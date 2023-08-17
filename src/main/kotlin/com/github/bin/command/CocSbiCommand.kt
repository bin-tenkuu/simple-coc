package com.github.bin.command

import com.github.bin.service.CocService
import com.github.bin.service.HisMsgService.Companion.sendAsBot
import com.github.bin.service.RoomConfig
import com.github.bin.util.CacheMap
import com.github.bin.util.DiceResult

/**
 * @author bin
 * @since 2023/08/17
 */
object CocSbiCommand {
    @JvmStatic
    val cache = CacheMap<Long?, DiceResult>()

    private fun sbiResult(list: IntArray): String {
        if (list.size < 3) {
            return "数量过少"
        }
        setOf(list[0], list[1], list[2]).sorted().apply {
            if (size == 1) {
                return "大失败"
            }
            if (size == 3 && sum() == 6) {
                return "大成功，成功度${list.count { 1 == it }}"
            }
        }
        val intArray = list.toSortedSet().toIntArray()
        val arr = intArrayOf(intArray[0], 0)
        for (i in intArray) {
            if (i - arr[0] == 1) {
                if (arr[1] == 1) {
                    return "成功，成功度${list.count { 1 == it }}"
                } else {
                    arr[1] = 1
                }
            } else {
                arr[1] = 0
            }
            arr[0] = i
        }
        return "失败"
    }

    // s
    class S : SimpleCommand("s") {
        private val diceRegex = Regex("""(?<num>\d*)d(?<max>\d*)""", RegexOption.IGNORE_CASE)

        override fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean {
            val dice = msg.split(" ").let {
                if (it.isEmpty()) return true
                it[0]
            }
            val matchResult = diceRegex.find(dice) ?: run {
                roomConfig.sendAsBot("骰子格式错误")
                return true
            }
            val groups = matchResult.groups
            val num = (groups["num"]!!.value.toIntOrNull() ?: 0).coerceAtLeast(3)
            val max = groups["max"]!!.value.toIntOrNull() ?: 0
            val diceResult = DiceResult(num, max)
            if (!CocService.cheater) diceResult.dice()
            val roleId = roomConfig.getRole(id)
            cache[roleId] = diceResult
            val msg = "${diceResult.origin}：[${diceResult.list.joinToString()}]（${sbiResult(diceResult.list)}）"
            roomConfig.sendAsBot(msg)
            return true
        }
    }

    // sp
    class Sp : SimpleCommand("sp") {
        override fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean {
            val roleId = roomConfig.getRole(id)
            val num = msg.split(" ").let {
                if (it.isEmpty()) return true
                it[0].toIntOrNull() ?: return true
            }
            var diceResult: DiceResult = cache[roleId] ?: run {
                roomConfig.sendAsBot("10分钟之内没有投任何骰子")
                return true
            }
            val dice = DiceResult(num, diceResult.max)
            if (!CocService.cheater) dice.dice()
            diceResult += dice
            cache[roleId] = diceResult
            roomConfig.sendAsBot(
                """${dice.origin}：[${dice.list.joinToString(", ")}]=${dice.sum}
                |[${diceResult.list.joinToString(", ")}]（${sbiResult(diceResult.list)}）
            """.trimMargin()
            )
            return true
        }
    }
}
