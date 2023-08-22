package com.github.bin.command;

import com.github.bin.service.RoomConfig;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author bin
 * @since 2023/08/22
 */
@RequiredArgsConstructor
public abstract class EqualCommand implements Command {
    protected final String msg;

    @Override
    public final boolean invoke(RoomConfig roomConfig, String id, String msg) {
        if (Objects.equals(this.msg, msg)) {
            return invoke(roomConfig, id);
        }
        return false;
    }

    abstract boolean invoke(RoomConfig roomConfig, String id);
}
