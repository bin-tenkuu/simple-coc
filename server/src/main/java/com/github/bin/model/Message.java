package com.github.bin.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.bin.entity.master.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
        value = {
                @JsonSubTypes.Type(value = Message.Default.class, name = Message.DEFAULT),
                @JsonSubTypes.Type(value = Message.Msgs.class, name = Message.MSGS),
                @JsonSubTypes.Type(value = Message.RoomMessage.class, name = Message.ROOM),
                @JsonSubTypes.Type(value = Message.Msg.class, names = {
                        Message.TEXT,
                        Message.PIC,
                        Message.SYS,
                }),
        },
        failOnRepeatedNames = true
)
public sealed interface Message
        permits Message.Default,
        Message.Msg,
        Message.Msgs, Message.RoomMessage {
    String DEFAULT = "default";
    String TEXT = "text";
    String PIC = "pic";
    String SYS = "sys";
    String MSGS = "msgs";
    String ROOM = "room";

    @Getter
    @Setter
    final class Default implements Message {
        private Long id;
        private Integer role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Msg implements Message {
        private String type;
        private Long id;
        private int role = -1;
        private String msg = "";

        public static Msg text(Long id, int role, String msg) {
            return new Msg(TEXT, id, role, msg);
        }

        public static Msg pic(Long id, int role, String msg) {
            return new Msg(PIC, id, role, msg);
        }

        public static Msg sys(Long id, int role, String msg) {
            return new Msg(SYS, id, role, msg);
        }
    }

    @Getter
    @AllArgsConstructor
    final class Msgs implements Message {
        private final List<Message> msgs;

        public Msgs() {
            this(Collections.emptyList());
        }
    }

    @Getter
    @AllArgsConstructor
    final class RoomMessage implements Message {
        private final Room room;
    }
}
