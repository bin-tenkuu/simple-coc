package com.github.bin.task;

import com.github.bin.repository.RoomService;
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
    @Scheduled(fixedRate = 7, timeUnit = TimeUnit.DAYS)
    public void gcRoom() {
        val sb = new StringBuilder();
        val iterator = RoomService.values().iterator();
        while (iterator.hasNext()) {
            val room = iterator.next();
            if (!room.isHold()) {
                room.close();
                iterator.remove();
                sb.append("\n").append("清理房间: ").append(room.getRoomId());
            } else if (room.isEmpty()) {
                room.unHold();
                sb.append("\n").append("准备清理房间: ").append(room.getRoomId());
            } else {
                sb.append("\n").append(room.getRoomId()).append(" : ").append(room.size()).append(" 个连接");
            }
        }
        if (!sb.isEmpty()) {
            log.info(sb.toString());
        }
    }
}
