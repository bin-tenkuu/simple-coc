package com.github.bin.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.extension.activerecord.Model
import com.github.bin.config.handler.RoomHandler
import jakarta.validation.constraints.NotBlank
import org.apache.ibatis.type.JdbcType

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@TableName("room", resultMap = "BaseResultMap")
class Room : Model<Room>() {
    @NotBlank
    var id: String? = null
    var name: String? = null

    @TableField("roles", jdbcType = JdbcType.OTHER, typeHandler = RoomHandler::class)
    var roles: MutableMap<String, Role> = HashMap()
    override fun pkVal() = id
}

class Role(
    val id: String,
    var name: String,
    var tags: MutableList<Tag>,
)

class Tag(
    val name: String,
    val type: String = "",
    val color: String = ""
)

@TableName("his_msg", resultMap = "BaseResultMap")
class HisMsg : Model<HisMsg>() {
    @TableId("id", type = IdType.AUTO)
    var id: Long? = null
    var type: String? = null
    var msg: String? = null
    var role: String? = null
    override fun pkVal() = id
}
