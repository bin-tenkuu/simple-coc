package com.github.bin.task;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

/**
 * @author bin
 * @since 2023/09/05
 */
@Component
@Slf4j
public class RoomLogTask {
    private static final String ROOM_LOG = "logs/room";

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanLog() {
        val file = new File(ROOM_LOG);
        if (!file.isDirectory()) {
            if (!file.mkdir()) {
                log.error("创建目录失败: {}", ROOM_LOG);
                return;
            }
        }
        val list = file.listFiles();
        if (list == null) {
            return;
        }
        for (val logFile : list) {
            val lastday = LocalDateTime.now().minusHours(12);
            val lastModefied = LocalDateTimeUtil.of(logFile.lastModified());
            if (lastModefied.isBefore(lastday)) {
                if (!logFile.delete()) {
                    log.error("删除文件失败: {}", logFile.getPath());
                }
            }
        }
    }
}
