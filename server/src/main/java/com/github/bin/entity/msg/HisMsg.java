package com.github.bin.entity.msg;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "his_msg", resultMap = "BaseResultMap")
public class HisMsg {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private MsgType type;
    private String msg;
    private Integer role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
