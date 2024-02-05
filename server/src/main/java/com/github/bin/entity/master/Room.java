package com.github.bin.entity.master;

import com.baomidou.mybatisplus.annotation.*;
import com.github.bin.config.handler.RoomHandler;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
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
    @NotNull
    @Pattern(regexp = "^\\w+$", message = "房间ID只能包含字母、数字、下划线")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @TableField("name")
    @NotBlank
    private String name;

    @TableField(value = "roles", jdbcType = JdbcType.VARCHAR, typeHandler = RoomHandler.class)
    private Map<Integer, RoomRole> roles = new HashMap<>();

    @TableField(value = "user_id", insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.NEVER)
    private Long userId;
    public static final Long ALL_USER = 0L;

    @TableField(value = "archive", insertStrategy = FieldStrategy.NEVER)
    private Boolean archive;

    @TableField(value = "update_date", update = "CURRENT_DATE")
    private LocalDate updateDate;

    @TableField(exist = false)
    private Boolean enable;

    public void addRole(RoomRole role) {
        roles.put(role.getId(), role);
    }

    public Room(Room room) {
        this.id = room.id;
        this.name = room.name;
        this.roles = room.roles;
        this.userId = room.userId;
        this.archive = room.archive;
        this.updateDate = room.updateDate;
        this.enable = room.enable;
    }
}

