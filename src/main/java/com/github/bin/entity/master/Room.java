package com.github.bin.entity.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.bin.config.handler.RoomHandler;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bin
 * @since 2023/08/22
 */
@Getter
@Setter
@NoArgsConstructor
@TableName(value = "room", resultMap = "BaseResultMap")
public class Room {
    @NotBlank
    @Pattern(regexp = "^\\w+$", message = "房间ID只能包含字母、数字、下划线")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @TableField("name")
    private String name;

    @TableField(value = "roles", jdbcType = JdbcType.VARCHAR, typeHandler = RoomHandler.class)
    private Map<Long, RoomRole> roles = new HashMap<>();

}

