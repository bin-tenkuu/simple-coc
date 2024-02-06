package com.github.bin.entity.msg;

import com.github.bin.enums.MsgType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author bin
 * @since 2023/08/22
 */
@Getter
@Setter
@NoArgsConstructor
public class HisMsg {
    private Long id;
    private MsgType type;
    private String msg;
    private Integer role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
