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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
        value = {
                @JsonSubTypes.Type(value = MessageIn.Default.class, name = MessageIn.DEFAULT),
                @JsonSubTypes.Type(value = MessageIn.Msg.class, names = {
                        MessageIn.TEXT,
                        MessageIn.SYS,
                }),
                @JsonSubTypes.Type(value = MessageIn.Top.class, name = MessageIn.TOP),
                @JsonSubTypes.Type(value = MessageIn.His.class, name = MessageIn.HIS),
        },
        failOnRepeatedNames = true
)
public sealed interface MessageIn {
    String DEFAULT = "default";
    String TEXT = "text";
    String SYS = "sys";
    String TOP = "top";
    String HIS = "his";

    @Getter
    @Setter
    final class Default implements MessageIn {
        private String roomId;
        private Integer id;
        private Integer role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    final class Msg implements MessageIn {
        private MsgType type;
        @Nullable
        private Integer id;
        private int role = -1;
        private String msg = "";
    }

    @Getter
    @Setter
    final class Top implements MessageIn {
        private String message;
        private String token;
    }

    @Getter
    @Setter
    final class His implements MessageIn {
    }
}
