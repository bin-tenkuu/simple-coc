package com.github.bin.command;

import com.github.bin.entity.master.RoomRole;
import com.github.bin.service.RoomConfig;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.MagicConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bin
 * @since 2023/08/22
 */
public interface Command {
    /**
     * 消息处理
     *
     * @param roomConfig RoomConfig
     * @param id String
     * @param msg 开头去除指令前缀，去除前后空格
     * @return 是否取消后续处理
     */
    boolean invoke(RoomConfig roomConfig, String id, String msg);

    /**
     * @author bin
     * @since 2023/08/22
     */
    @RequiredArgsConstructor
    abstract class Equal implements Command {
        protected final String msg;

        @Override
        public final boolean invoke(RoomConfig roomConfig, String id, String msg) {
            val roomRole = roomConfig.getRole(id);
            if (roomRole == null) {
                return true;
            }
            if (this.msg.equals(msg)) {
                return handler(roomConfig, id, roomRole);
            }
            return false;
        }

        protected abstract boolean handler(RoomConfig roomConfig, String id, RoomRole roomRole);
    }

    /**
     * @author bin
     * @since 2023/08/22
     */
    @RequiredArgsConstructor
    abstract class Simple implements Command {
        protected final String first;

        @Override
        public final boolean invoke(RoomConfig roomConfig, String id, String msg) {
            val roomRole = roomConfig.getRole(id);
            if (roomRole == null) {
                return true;
            }
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

        protected abstract boolean handler(RoomConfig roomConfig, String id, String msg);
    }

    /**
     * @author bin
     * @since 2023/08/22
     */
    @RequiredArgsConstructor
    abstract class Regex implements Command {
        private final Pattern regex;

        public Regex(@Language("RegExp") String regex) {
            this(regex, 0);
        }

        public Regex(
                @Language("RegExp") @NotNull String regex,
                @MagicConstant(flagsFromClass = Pattern.class) int flags
        ) {
            this.regex = Pattern.compile(regex, flags);
        }

        @Override
        public final boolean invoke(RoomConfig roomConfig, String id, String msg) {
            val roomRole = roomConfig.getRole(id);
            if (roomRole == null) {
                return true;
            }
            val result = regex.matcher(msg);
            if (result.find()) {
                return handler(roomConfig, id, result, roomRole);
            }
            return false;
        }

        protected abstract boolean handler(RoomConfig roomConfig, String id, Matcher matcher, RoomRole roomRole);
    }
}
