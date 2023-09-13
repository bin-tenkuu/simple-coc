package com.github.bin.command;

import com.github.bin.entity.master.RoomRole;
import com.github.bin.service.RoomConfig;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

/**
 * @author bin
 * @since 2023/08/23
 */
public interface HelperScope {
    @Component
    class Ping extends Command.Regex {
        public Ping() {
            super("^ping$");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole) {
            roomConfig.sendAsBot("pong");
            return true;
        }
    }
}
