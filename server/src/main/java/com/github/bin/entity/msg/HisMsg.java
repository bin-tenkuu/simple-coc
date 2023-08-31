package com.github.bin.entity.msg;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String type;
    private String msg;
    private Integer role;
}
