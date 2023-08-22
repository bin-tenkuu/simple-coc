package com.github.bin.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.bin.entity.master.RoomRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                @JsonSubTypes.Type(value = Message.Roles.class, name = Message.ROLES),
        },
        failOnRepeatedNames = true
)
public sealed interface Message
        permits Message.Msg, Message.Default, Message.Msgs, Message.Pic, Message.Roles, Message.Sys, Message.Text {
    String DEFAULT = "default";
    String TEXT = "text";
    String PIC = "pic";
    String SYS = "sys";
    String MSGS = "msgs";
    String ROLES = "roles";

    sealed interface Msg extends Message {
        String getType();

        Long getId();

        void setId(Long id);

        long getRole();

        void setRole(long role);

        String getMsg();

        void setMsg(String msg);
    }

    @Getter
    @Setter
    final class Default implements Message {
        private Long id;
        private long role = -1L;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Text implements Message, Msg {
        private final String type = TEXT;
        private Long id;
        private long role = -1;
        private String msg = "";

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Pic implements Message, Msg {
        private final String type = PIC;
        private Long id;
        private long role = -1;
        private String msg = "";

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Sys implements Message, Msg {
        private final String type = SYS;
        private Long id;
        private long role = -1;
        private String msg = "";
    }

    record Msgs(List<Message> msgs) implements Message {
        public Msgs() {
            this(new ArrayList<>(0));
        }
    }

    record Roles(Map<Long, RoomRole> roles) implements Message {

    }
}
