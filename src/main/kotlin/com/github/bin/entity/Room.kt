package com.github.bin.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.github.bin.config.handler.RoomHandler
import jakarta.validation.constraints.NotBlank
import org.apache.ibatis.type.JdbcType

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@TableName("room", resultMap = "BaseResultMap")
class Room {
    @NotBlank
    @TableId("id", type = IdType.AUTO)
    var id: String? = null

    @TableField("name")
    var name: String? = null

    @TableField("roles", jdbcType = JdbcType.OTHER, typeHandler = RoomHandler::class)
    var roles: MutableMap<String, RoomRole> = HashMap()
}

@TableName("room_role", resultMap = "BaseResultMap")
class RoomRole(
        @TableId("id", type = IdType.AUTO)
        val id: String,
        @TableField("name")
        var name: String,
        @TableField("color")
        var color: String,
)

@TableName("his_msg", resultMap = "BaseResultMap")
class HisMsg {
    @TableId("id", type = IdType.AUTO)
    var id: Long? = null
    var type: String? = null
    var msg: String? = null
    var role: String? = null
}
