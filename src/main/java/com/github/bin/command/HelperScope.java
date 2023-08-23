package com.github.bin.command;

import com.github.bin.service.RoomConfig;
import org.springframework.stereotype.Component;

/**
 * @author bin
 * @since 2023/08/23
 */
public interface HelperScope {
    @Component
    class Ping extends Command.Simple {
        public Ping() {
            super("ping");
        }

        @Override
        protected boolean handler(RoomConfig roomConfig, String id, String msg) {
            roomConfig.sendAsBot("pong");
            return true;
        }
    }
}
