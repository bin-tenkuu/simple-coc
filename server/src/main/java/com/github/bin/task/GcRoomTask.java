package com.github.bin.task;

import com.github.bin.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
@Slf4j
public class GcRoomTask {
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void gcRoom() {
        val iterator = RoomService.values().iterator();
        while (iterator.hasNext()) {
            val room = iterator.next();
            if (!room.isHold()) {
                log.info("清理房间: {}", room.getId());
                iterator.remove();
            } else if (room.isEmpty()) {
                log.info("准备清理房间: {}", room.getId());
                room.unhold();
            }
        }
    }
}
