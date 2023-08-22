package com.github.bin.command

import com.github.bin.service.HisMsgService.sendAsBot
import com.github.bin.service.RoomConfig
import org.springframework.stereotype.Component

object HelperCommand {
    @Component
    class Ping : SimpleCommand("ping") {
        override fun handler(roomConfig: RoomConfig, id: String, msg: String): Boolean {
            sendAsBot(roomConfig, "pong")
            return true
        }
    }
}
