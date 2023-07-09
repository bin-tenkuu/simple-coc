package com.github.bin.task

import com.github.bin.service.RoomService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/2
 */
@Component
class GcRoomTask {
    private val logger = LoggerFactory.getLogger(GcRoomTask::class.java)

    @Scheduled(cron = "0 0 * * * ?")
    fun gcRoom() {
        val iterator = RoomService.values.iterator()
        while (iterator.hasNext()) {
            val room = iterator.next()
            if (!room.hold) {
                logger.info("清理房间: {}", room.id)
                iterator.remove()
            }
        }
    }
}
