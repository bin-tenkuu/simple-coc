package com.github.bin.command;

import com.github.bin.service.RoomConfig;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bin
 * @since 2023/08/22
 */
@RequiredArgsConstructor
public abstract class RegexCommand implements Command {
    private final Pattern regex;

    @Override
    public final boolean invoke(RoomConfig roomConfig, String id, String msg) {
        val result = regex.matcher(msg);
        if (result.find()) {
            return handler(roomConfig, id, result);
        }
        return false;
    }

    abstract boolean handler(RoomConfig roomConfig, String id, Matcher msg);
}
