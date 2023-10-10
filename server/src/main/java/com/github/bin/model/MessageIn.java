package com.github.bin.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.bin.enums.MsgType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * @author bin
 * @since 2023/08/22
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes(
        value = {
                @JsonSubTypes.Type(value = MessageIn.Default.class, name = MessageIn.DEFAULT),
                @JsonSubTypes.Type(value = MessageIn.Msg.class, names = {
                        MessageIn.TEXT,
                        MessageIn.PIC,
                        MessageIn.SYS,
                }),
        },
        failOnRepeatedNames = true
)
public sealed interface MessageIn {
    String DEFAULT = "default";
    String TEXT = "text";
    String PIC = "pic";
    String SYS = "sys";

    @Getter
    @Setter
    final class Default implements MessageIn {
        private String roomId;
        private Long id;
        private Integer role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Msg implements MessageIn {
        private MsgType type;
        @Nullable
        private Long id;
        private int role = -1;
        private String msg = "";
    }
}
