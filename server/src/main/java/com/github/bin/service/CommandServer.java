package com.github.bin.service;

import com.github.bin.command.CocSbiScope;
import com.github.bin.command.CocScope;
import com.github.bin.command.Command;
import com.github.bin.command.HelperScope;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/16
 */
@Component
@Slf4j
public class CommandServer implements InitializingBean {
    private static final List<Command> COMMANDS = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        COMMANDS.addAll(HelperScope.getCommands());
        COMMANDS.addAll(CocScope.getCommands());
        COMMANDS.addAll(CocSbiScope.getCommands());
    }

    public static void handleBot(RoomConfig roomConfig, String id, String msg) {
        try {
            for (val command : COMMANDS) {
                if (command.invoke(roomConfig, id, msg)) {
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("bot 处理异常", e);
            roomConfig.sendAsBot("【ERROR】bot 处理异常，请联系管理员");
        }
    }

}
