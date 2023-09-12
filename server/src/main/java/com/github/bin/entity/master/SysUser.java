package com.github.bin.entity.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author bin
 * @since 2023/09/11
 */
@Getter
@Setter
@NoArgsConstructor
@TableName(value = "sys_user", resultMap = "BaseResultMap")
public class SysUser {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_name")
    private String username;
    @TableField("nick_name")
    private String nickname;
    @TableField("password")
    private String password;
    @TableField("salt")
    private String salt;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
}
