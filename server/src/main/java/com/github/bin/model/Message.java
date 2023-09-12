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
                @JsonSubTypes.Type(value = Message.Text.class, name = Message.TEXT),
                @JsonSubTypes.Type(value = Message.Pic.class, name = Message.PIC),
                @JsonSubTypes.Type(value = Message.Sys.class, name = Message.SYS),
                @JsonSubTypes.Type(value = Message.Msgs.class, name = Message.MSGS),
                @JsonSubTypes.Type(value = Message.RoomMessage.class, name = Message.ROOM),
        },
        failOnRepeatedNames = true
)
public sealed interface Message
        permits Message.Default,
        Message.Msg, Message.Text, Message.Pic, Message.Sys,
        Message.Msgs, Message.RoomMessage {
    String DEFAULT = "default";
    String TEXT = "text";
    String PIC = "pic";
    String SYS = "sys";
    String MSGS = "msgs";
    String ROOM = "room";

    sealed interface Msg extends Message {
        String getType();

        Long getId();

        void setId(Long id);

        int getRole();

        void setRole(int role);

        String getMsg();

        void setMsg(String msg);
    }

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
    final class Text implements Message, Msg {
        private final String type = TEXT;
        private Long id;
        private int role = -1;
        private String msg = "";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Pic implements Message, Msg {
        private final String type = PIC;
        private Long id;
        private int role = -1;
        private String msg = "";

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Sys implements Message, Msg {
        private final String type = SYS;
        private Long id;
        private int role = -1;
        private String msg = "";
    }

    @Getter
    @AllArgsConstructor
    final class Msgs implements Message {
        private final List<Message> msgs;

        public Msgs() {
            this(Collections.emptyList());
        }
    }

    record RoomMessage(Room room) implements Message {
    }
}
