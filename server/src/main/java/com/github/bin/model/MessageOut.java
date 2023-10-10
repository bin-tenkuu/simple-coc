package com.github.bin.model;

import com.github.bin.entity.master.Room;
import com.github.bin.enums.ElPosition;
import com.github.bin.enums.ElType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bin
 * @since 2023/10/10
 */
public sealed interface MessageOut {
    String ROOM = "room";
    String NOTIFY = "notify";

    String getType();

    @Getter
    @AllArgsConstructor
    final class RoomMessage implements MessageOut {
        private final String type = ROOM;
        private final Room room;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class ElNotification implements MessageOut {
        private final String type = NOTIFY;
        private final String title;
        private final String message;
        private final String elType;
        private final String position;

        public static ElNotification of(String title, String message, ElType elType, ElPosition position) {
            return new ElNotification(title, message, elType.getValue(), position.getValue());
        }
    }

    @Getter
    @AllArgsConstructor
    final class Msg implements MessageOut {
        private final String type;
        private final Long id;
        private final int role;
        private final String msg;
    }

}
