package com.github.bin.command;

import com.github.bin.service.RoomConfig;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author bin
 * @since 2023/08/22
 */
@RequiredArgsConstructor
public abstract class SimpleCommand implements Command {
    protected final String first;

    @Override
    public final boolean invoke(RoomConfig roomConfig, String id, String msg) {
        val split = test(msg);
        if (split == null) {
            return false;
        }
        return handler(roomConfig, id, split);
    }

    protected String test(String split) {
        if (split.startsWith(first)) {
            return split.substring(first.length());
        } else {
            return null;
        }
    }

    abstract boolean handler(RoomConfig roomConfig, String id, String msg);
}
