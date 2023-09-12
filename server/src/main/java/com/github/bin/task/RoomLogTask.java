package com.github.bin.task;

import com.github.bin.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author bin
 * @since 2023/09/05
 */
@Component
@Slf4j
public class RoomLogTask extends Thread implements InitializingBean {
    @Scheduled(cron = "0 0/10 * * * ?")
    public void cleanLog() {
        val currentTimeMillis = System.currentTimeMillis();
        val iterator = RoomService.fileIter();
        while (iterator.hasNext()) {
            val entry = iterator.next();
            if (entry.getValue() < currentTimeMillis) {
                if (removeFile(entry.getKey())) {
                    iterator.remove();
                }
            }
        }
    }

    private static boolean removeFile(File file) {
        if (file.delete()) {
            log.info("删除文件: {}", file.getName());
            return true;
        } else {
            log.error("删除文件失败: {}", file.getName());
            return false;
        }
    }

    @Override
    public void run() {
        val iterator = RoomService.fileIter();
        while (iterator.hasNext()) {
            removeFile(iterator.next().getKey());
        }
    }

    @Override
    public void afterPropertiesSet() {
        Runtime.getRuntime().addShutdownHook(this);
    }
}
