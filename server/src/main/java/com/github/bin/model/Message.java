package com.github.bin.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.bin.entity.master.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author bin
 * @since 2023/08/22
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes(
        value = {
                @JsonSubTypes.Type(value = Message.Default.class, name = Message.DEFAULT),
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
        Message.RoomMessage {
    String DEFAULT = "default";
    String TEXT = "text";
    String PIC = "pic";
    String SYS = "sys";
    String ROOM = "room";

    enum MsgType {
        text,
        pic,
        sys,
        ;

        public Msg create(Long id, int role, String msg) {
            return new Msg(name(), id, role, msg);
        }

        public Msg create(int role, String msg) {
            return new Msg(name(), null, role, msg);
        }
    }

    @Getter
    @Setter
    final class Default implements Message {
        private String roomId;
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
    }

    @Getter
    @AllArgsConstructor
    final class RoomMessage implements Message {
        private final Room room;
    }
}
